package com.kevalpatel2106.standup.dashboard


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.timelineview.TimeLineItem
import com.kevalpatel2106.standup.timelineview.TimeLineLength
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.standup.userActivity.repo.UserActivityRepoImpl
import com.kevalpatel2106.utils.ViewUtils
import io.reactivex.FlowableSubscriber
import kotlinx.android.synthetic.main.layout_home_efficiency_card.*
import kotlinx.android.synthetic.main.layout_home_timeline_card.*
import org.reactivestreams.Subscription
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    companion object {

        /**
         * Get the new instance of [DashboardFragment].
         */
        fun getNewInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO Remove this
        UserActivityRepoImpl().getTodayEvents().subscribe(object : FlowableSubscriber<List<UserActivity>> {
            override fun onError(t: Throwable?) {
                Timber.d("Error occurred.")
            }

            override fun onNext(t: List<UserActivity>?) {
                Timber.d("onNext")
                Timber.d(t?.size.toString())
            }

            override fun onSubscribe(s: Subscription) {
            }

            override fun onComplete() {
            }

        })

        setEfficiencyCard()
        setTimelineCard()
    }

    /**
     * Set up the efficiency card.
     */
    private fun setEfficiencyCard() {
        /* Set the pie chart */
        home_efficiency_card_pie_chart.setDrawCenterText(false) //Don't want to draw text on center
        home_efficiency_card_pie_chart.description.isEnabled = false    //Don't want to display any description
        home_efficiency_card_pie_chart.setUsePercentValues(true)    //All the values in %.
        home_efficiency_card_pie_chart.setDrawEntryLabels(false)
        home_efficiency_card_pie_chart.animateY(AppConfig.PIE_CHART_TIME, Easing.EasingOption.EaseInOutQuad)
        home_efficiency_card_pie_chart.setDrawEntryLabels(false)

        //The hole in the middle
        home_efficiency_card_pie_chart.isDrawHoleEnabled = true //Display the hole in the middle
        home_efficiency_card_pie_chart.holeRadius = 40F //Keep the hole radius 40% of total chart radius
        home_efficiency_card_pie_chart.transparentCircleRadius = home_efficiency_card_pie_chart.holeRadius  //Keep the transparent radius 40% of total chart radius
        home_efficiency_card_pie_chart.setHoleColor(Color.TRANSPARENT)  //Keep hole transparent.

        //Set rotation
        home_efficiency_card_pie_chart.isRotationEnabled = true //User can rotate by touching
        home_efficiency_card_pie_chart.rotationAngle = 0F   //Initially set the chart at 0 degree
        home_efficiency_card_pie_chart.dragDecelerationFrictionCoef = 0.95f
        home_efficiency_card_pie_chart.isHighlightPerTapEnabled = true  //Tapping on any segment of chart will highlight it.

        //Set legends
        home_efficiency_card_pie_chart.legend.isEnabled = true

        val l = home_efficiency_card_pie_chart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.textColor = Color.WHITE
        l.textSize = ViewUtils.toPx(context!!, 5).toFloat()

        //Prepare the values in the pie chart.
        val entries = ArrayList<PieEntry>(2)
        entries.add(PieEntry(69F /*TODO Set real value*/, "Sitting"))   //Sitting time percentage
        entries.add(PieEntry(31F /*TODO Set real value*/, "Standing"))  //Standing time percentage

        //Prepare the colors for each segment in the pie chart.
        val colors = ArrayList<Int>(2)
        colors.add(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))       //Chart color for sitting segment
        colors.add(ContextCompat.getColor(context!!, android.R.color.holo_orange_dark)) //Chart color for standing segment

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.colors = colors
        dataSet.sliceSpace = 2F
        dataSet.setAutomaticallyDisableSliceSpacing(true)
        dataSet.setDrawValues(true)

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(16f)
        data.setValueTextColor(Color.WHITE)

        home_efficiency_card_pie_chart.data = data
        home_efficiency_card_pie_chart.highlightValue(null)
        home_efficiency_card_pie_chart.invalidate()
    }

    /**
     * Set up the time line card.
     */
    private fun setTimelineCard() {
        //Timeline view
        today_time_line.timelineDuration = TimeLineLength.A_DAY

        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTime = 3600,
                endTime = 2 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = (2.5 * 3600).toLong(),
                endTime = 3 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 3 * 3600,
                endTime = (3.25 * 3600).toLong(),
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 6 * 3600,
                endTime = 8 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 5 * 3600,
                endTime = (5.90 * 3600).toLong(),
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 12 * 3600,
                endTime = 23 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        today_time_line.timelineItems = timelineItems
    }
}
