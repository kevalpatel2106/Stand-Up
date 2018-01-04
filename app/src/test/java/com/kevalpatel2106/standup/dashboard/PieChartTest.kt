/*
 *  Copyright 2017 Keval Patel.
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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.graphics.Color
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.formatter.PercentFormatter
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepo
import com.kevalpatel2106.standup.dashboard.repo.DashboardRepoImpl
import com.kevalpatel2106.standup.db.userActivity.UserActivityDao
import com.kevalpatel2106.standup.db.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Keval on 04/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class PieChartTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private val userActivityDao: UserActivityDao = UserActivityDaoMockImpl(ArrayList())
    private val mockServerManager = MockServerManager()
    private lateinit var dashboardRepo: DashboardRepo
    private lateinit var model: DashboardViewModel

    @Before
    fun setUp() {
        mockServerManager.startMockWebServer()

        dashboardRepo = DashboardRepoImpl(userActivityDao, mockServerManager.getBaseUrl())
        model = DashboardViewModel(dashboardRepo)
    }


    @Test
    fun checkSetPieChart() {
        val pieChart = PieChart(RuntimeEnvironment.application)

        model.setPieChart(RuntimeEnvironment.application, pieChart)

        Assert.assertFalse(pieChart.isDrawCenterTextEnabled)
        Assert.assertFalse(pieChart.description.isEnabled)
        Assert.assertTrue(pieChart.isUsePercentValuesEnabled)
        Assert.assertTrue(pieChart.isDrawHoleEnabled)
        Assert.assertEquals(pieChart.holeRadius, 50F)
        Assert.assertTrue(pieChart.isRotationEnabled)
        Assert.assertEquals(pieChart.rotationAngle, 0F)
        Assert.assertEquals(pieChart.dragDecelerationFrictionCoef, 0.95F)
        Assert.assertTrue(pieChart.isHighlightPerTapEnabled)

        Assert.assertTrue(pieChart.legend.isEnabled)
        Assert.assertEquals(pieChart.legend.verticalAlignment, Legend.LegendVerticalAlignment.TOP)
        Assert.assertEquals(pieChart.legend.horizontalAlignment, Legend.LegendHorizontalAlignment.RIGHT)
        Assert.assertEquals(pieChart.legend.orientation, Legend.LegendOrientation.VERTICAL)
        Assert.assertEquals(pieChart.legend.textColor, Color.WHITE)
    }

    @Test
    fun checkSettingPieChartDateWithData() {
        val pieChart = PieChart(RuntimeEnvironment.application)

        model.setPieChart(RuntimeEnvironment.application, pieChart)
        model.setPieChartData(RuntimeEnvironment.application, pieChart, 40F, 60F)

        Assert.assertTrue(pieChart.isHighlightPerTapEnabled)

        val pieData = pieChart.data
        val dataSet = pieData.dataSet

        Assert.assertTrue(dataSet.valueFormatter is PercentFormatter)
        Assert.assertEquals(dataSet.valueTextColor, Color.WHITE)
        Assert.assertEquals(dataSet.valueTextSize, 16F)
        Assert.assertEquals(dataSet.sliceSpace, 2F)
        Assert.assertTrue(dataSet.isDrawValuesEnabled)
        Assert.assertTrue(dataSet.isAutomaticallyDisableSliceSpacingEnabled)

        //Check for the color
        val colorList = dataSet.colors
        Assert.assertEquals(2, colorList.size)

        //Check for the data set entry
        Assert.assertEquals(pieData.entryCount, 2)
        Assert.assertEquals(dataSet.getEntryForIndex(0).value, 40F)
        Assert.assertEquals(dataSet.getEntryForIndex(0).label, "Sitting")
        Assert.assertEquals(dataSet.getEntryForIndex(1).value, 60F)
        Assert.assertEquals(dataSet.getEntryForIndex(1).label, "Standing")
    }


    @Test
    fun checkSettingPieChartDateWithNoData() {
        val pieChart = PieChart(RuntimeEnvironment.application)

        model.setPieChart(RuntimeEnvironment.application, pieChart)
        model.setPieChartData(RuntimeEnvironment.application, pieChart, 0F, 0F)

        Assert.assertFalse(pieChart.isHighlightPerTapEnabled)

        val pieData = pieChart.data
        val dataSet = pieData.dataSet

        Assert.assertTrue(dataSet.valueFormatter is PercentFormatter)
        Assert.assertEquals(dataSet.valueTextColor, Color.WHITE)
        Assert.assertEquals(dataSet.valueTextSize, 16F)
        Assert.assertEquals(dataSet.sliceSpace, 2F)
        Assert.assertFalse(dataSet.isDrawValuesEnabled)
        Assert.assertTrue(dataSet.isAutomaticallyDisableSliceSpacingEnabled)

        //Check for the color
        val colorList = dataSet.colors
        Assert.assertEquals(3, colorList.size)

        //Check for the data set entry
        Assert.assertEquals(pieData.entryCount, 3)
        Assert.assertEquals(dataSet.getEntryForIndex(0).value, 0F)
        Assert.assertEquals(dataSet.getEntryForIndex(0).label, "Sitting")
        Assert.assertEquals(dataSet.getEntryForIndex(1).value, 0F)
        Assert.assertEquals(dataSet.getEntryForIndex(1).label, "Standing")
        Assert.assertEquals(dataSet.getEntryForIndex(2).value, 100F)
        Assert.assertEquals(dataSet.getEntryForIndex(2).label, "Not tracked")
    }
}