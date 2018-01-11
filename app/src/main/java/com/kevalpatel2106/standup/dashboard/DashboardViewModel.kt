/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.standup.dashboard

import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.graphics.Color
import android.support.annotation.VisibleForTesting
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.CallbackEvent
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.dashboard.di.DaggerDashboardComponent
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepo
import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.misc.SUUtils
import com.kevalpatel2106.standup.timelineview.TimeLineItem
import com.kevalpatel2106.utils.ViewUtils
import com.kevalpatel2106.utils.getColorCompat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 21/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DashboardViewModel : BaseViewModel {

    @Inject lateinit var userActivityRepo: DashboardRepo

    /**
     * Private constructor to add the custom [DashboardRepo] for testing.
     *
     * @param dashboardRepo Add your own [DashboardRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(dashboardRepo: DashboardRepo) : super() {
        this.userActivityRepo = dashboardRepo
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerDashboardComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build().inject(this@DashboardViewModel)
    }

    val todaySummary = MutableLiveData<DailyActivitySummary>()
    val todaySummaryErrorCallback = MutableLiveData<ErrorMessage>()
    val todaySummaryStartLoading = CallbackEvent()
    val timelineEventsList = MutableLiveData<ArrayList<TimeLineItem>>()

    internal fun getTodaysSummary() {
        userActivityRepo.getTodaySummary()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    todaySummaryStartLoading.dispatch()
                    todaySummaryErrorCallback.value = null
                }
                .doOnComplete {
                    if (timelineEventsList.value == null)
                        todaySummaryErrorCallback.value = ErrorMessage(R.string.dashboard_today_summary_not_fount)
                }
                .subscribe({
                    todaySummary.value = it

                    //Prepare the timeline data
                    val timelineItems = ArrayList<TimeLineItem>(it.dayActivity.size)
                    it.dayActivity.forEach { timelineItems.add(SUUtils.createTimeLineItemFromUserActivity(it)) }
                    timelineEventsList.value = timelineItems
                }, {
                    //Error message
                    todaySummaryErrorCallback.value = ErrorMessage(it.message)
                })
    }

    /**
     * Set up the efficiency card.
     */
    internal fun setPieChart(context: Context, pieChart: PieChart) {
        /* Set the pie chart */
        pieChart.setDrawCenterText(false) //Don't want to draw text on center
        pieChart.description.isEnabled = false    //Don't want to display any description
        pieChart.setUsePercentValues(true)    //All the values in %.
        pieChart.animateY(AppConfig.PIE_CHART_TIME, Easing.EasingOption.EaseInOutQuad)
        pieChart.setDrawEntryLabels(false)

        //The hole in the middle
        pieChart.isDrawHoleEnabled = true //Display the hole in the middle
        pieChart.holeRadius = 50F //Keep the hole radius 40% of total chart radius
        pieChart.transparentCircleRadius = pieChart.holeRadius  //Keep the transparent radius 40% of total chart radius
        pieChart.setHoleColor(Color.TRANSPARENT)  //Keep hole transparent.

        //Set rotation
        pieChart.isRotationEnabled = true //User can rotate by touching
        pieChart.rotationAngle = 0F   //Initially set the chart at 0 degree
        pieChart.dragDecelerationFrictionCoef = 0.95f
        pieChart.isHighlightPerTapEnabled = true  //Tapping on any segment of chart will highlight it.

        //Set legends
        pieChart.legend.isEnabled = true

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.textColor = Color.WHITE
        l.textSize = ViewUtils.toPx(context, 5).toFloat()
    }

    internal fun setPieChartData(context: Context,
                                 pieChart: PieChart,
                                 sittingDurationPercent: Float,
                                 standingDurationPercent: Float) {

        val isValueToDisplay = standingDurationPercent + sittingDurationPercent == 0F
        //Prepare the values in the pie chart.
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(sittingDurationPercent, "Sitting"))   //Sitting time percentage
        entries.add(PieEntry(standingDurationPercent, "Standing"))  //Standing time percentage

        //Prepare the colors for each segment in the pie chart.
        val colors = ArrayList<Int>()
        colors.add(context.getColorCompat(android.R.color.holo_green_dark))  //Chart color for sitting segment
        colors.add(context.getColorCompat(android.R.color.holo_orange_dark)) //Chart color for standing segment

        if (isValueToDisplay) {
            entries.add(PieEntry(100F, "Not tracked"))  //Standing time percentage
            colors.add(context.getColorCompat(android.R.color.darker_gray)) //Chart color for standing segment
        }
        pieChart.isHighlightPerTapEnabled = !isValueToDisplay

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.colors = colors
        dataSet.sliceSpace = 2F
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        dataSet.setDrawValues(!isValueToDisplay)

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(16f)
        data.setValueTextColor(Color.WHITE)

        pieChart.data = data
        pieChart.highlightValue(null)
        pieChart.invalidate()
    }
}
