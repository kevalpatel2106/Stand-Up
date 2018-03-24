package com.standup.app.settings.dailyReview

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.common.prefs.SharedPreferenceKeys
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.TestActivity
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.settings.R
import com.standup.core.Core
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 24-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class DailyReviewSettingsViewModelUiTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule<TestActivity>(TestActivity::class.java)

    @Rule
    @JvmField
    val rule1 = InstantTaskExecutorRule()

    private lateinit var model: DailyReviewSettingsViewModel
    private lateinit var sharedPrefsProvider: SharedPrefsProvider

    @Before
    fun setUp() {
        sharedPrefsProvider = SharedPrefsProvider(MockSharedPreference())
        val userSettingsManager = UserSettingsManager(sharedPrefsProvider)
        userSettingsManager.dailyReviewTimeFrom12Am = 8 * TimeUtils.ONE_HOUR_MILLS

        model = DailyReviewSettingsViewModel(
                core = Core(
                        userSessionManager = UserSessionManager(sharedPrefsProvider),
                        sharedPrefsProvider = sharedPrefsProvider,
                        userSettingsManager = UserSettingsManager(sharedPrefsProvider)
                ),
                settingsManager = userSettingsManager
        )
    }

    @Test
    @Throws(Exception::class)
    fun checkDatePickerDisplayed() {
        sharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, true)

        model.displayDateDialog(rule.activity, rule.activity.supportFragmentManager)

        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_time_picker_dialog))
                .check(matches(isDisplayed()))
    }

    @Test
    @Throws(Exception::class)
    fun checkPreselectedDate() {
        sharedPrefsProvider.savePreferences(SharedPreferenceKeys.PREF_KEY_DAILY_REVIEW_ENABLE, true)

        model.displayDateDialog(rule.activity, rule.activity.supportFragmentManager)

        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_hours))
                .check(matches(withText("8")))

        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_minutes))
                .check(matches(withText("00")))

        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_am_label))
                .check(matches(hasTextColor(R.color.colorPrimaryText)))

        onView(withId(com.philliphsu.bottomsheetpickers.R.id.bsp_pm_label))
                .check(matches(hasTextColor(R.color.colorPrimaryLight)))
    }
}
