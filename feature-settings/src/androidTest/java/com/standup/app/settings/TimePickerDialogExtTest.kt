package com.standup.app.settings

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.hasTextColor
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.TestActivity
import com.kevalpatel2106.utils.TimeUtils
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

/**
 * Created by Kevalpatel2106 on 24-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class TimePickerDialogExtTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    val calendar = TimeUtils.todayMidnightCal()

    @Before
    fun setUp() {

        calendar.add(Calendar.HOUR, 1)

        val dialog = GridTimePickerDialog.newInstance(
                { _, _, _ ->
                    //Do nothing
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        )
        dialog.setApplicationTheme(rule.activity)
        dialog.show(rule.activity.supportFragmentManager, "Test")
    }

    @Test
    @Throws(Exception::class)
    fun checkSelectedAmPmSelectedColor() {
        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_am_label))
                .check(matches(hasTextColor(R.color.colorPrimaryText)))

    }

    @Test
    @Throws(Exception::class)
    fun checkSelectedAmPmUnselectedColor() {
        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_pm_label))
                .check(matches(hasTextColor(R.color.colorPrimaryLight)))
    }

    @Test
    @Throws(Exception::class)
    fun checkHeaderSelectedColor() {
        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_hours))
                .check(matches(hasTextColor(R.color.colorPrimaryText)))
    }

    @Test
    @Throws(Exception::class)
    fun checkHeaderUnselectedColor() {
        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_minutes))
                .check(matches(hasTextColor(R.color.colorPrimaryLight)))
    }

    @Test
    @Throws(Exception::class)
    fun checkTimeSeparator() {
        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_separator))
                .check(matches(hasTextColor(R.color.colorAccent)))
    }
}
