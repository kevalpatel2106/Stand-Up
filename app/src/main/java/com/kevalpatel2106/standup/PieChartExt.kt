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

package com.kevalpatel2106.standup

import android.content.Context
import android.graphics.Color
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.kevalpatel2106.utils.ViewUtils
import com.kevalpatel2106.utils.getColorCompat

/**
 * Created by Kevalpatel2106 on 22-Jan-18.
 * Class that contains extension functions for different charts.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

/**
 * Set the theme parameters and dimensions for [PieChart].
 *
 * @see PieChart
 */
fun PieChart.setPieChart(context: Context) {
    /* Set the pie chart */
    setDrawCenterText(false) //Don't want to draw text on center
    description.isEnabled = false    //Don't want to display any description
    setUsePercentValues(true)    //All the values in %.
    animateY(context.resources.getInteger(R.integer.pie_chart_animation_duration), Easing.EasingOption.EaseInOutQuad)
    setDrawEntryLabels(false)

    //The hole in the middle
    isDrawHoleEnabled = true //Display the hole in the middle
    holeRadius = 50F //Keep the hole radius 40% of total chart radius
    transparentCircleRadius = holeRadius  //Keep the transparent radius 40% of total chart radius
    setHoleColor(Color.TRANSPARENT)  //Keep hole transparent.

    //Set rotation
    isRotationEnabled = true //User can rotate by touching
    rotationAngle = 0F   //Initially set the chart at 0 degree
    dragDecelerationFrictionCoef = 0.95f
    isHighlightPerTapEnabled = true  //Tapping on any segment of chart will highlight it.

    //Set legends
    legend.isEnabled = true

    val l = legend
    l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
    l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
    l.orientation = Legend.LegendOrientation.VERTICAL
    l.setDrawInside(false)
    l.textColor = Color.WHITE
    l.textSize = ViewUtils.toPx(context, 5).toFloat()
}

/**
 * Set the data for [PieChart] that will display [sittingDurationPercent] and [standingDurationPercent]
 * with different colors. If the addition of [sittingDurationPercent] and [standingDurationPercent]
 * is zero, [PieChart] will display third "Not Tracked" [PieEntry] with dark gray color.
 *
 * @see PieChart
 * @see PieDataSet
 */
fun PieChart.setPieChartData(context: Context,
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
    isHighlightPerTapEnabled = !isValueToDisplay

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

    this.data = data
    highlightValue(null)
    invalidate()
}
