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

package com.standup.app.settings.list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.kevalpatel2106.common.userActivity.UserActivity
import com.kevalpatel2106.common.userActivity.UserActivityDao
import com.kevalpatel2106.common.userActivity.UserActivityDaoMockImpl
import com.kevalpatel2106.common.userActivity.UserActivityType
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.settings.R
import com.standup.app.settings.whitelisting.WhitelistingUtils
import io.reactivex.observers.TestObserver
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 09-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SettingsViewModelTest {

    @JvmField
    @Rule
    val rule = RxSchedulersOverrideRule()

    @JvmField
    @Rule
    val rule1 = InstantTaskExecutorRule()

    private lateinit var mockUserActivityDao: UserActivityDao
    private lateinit var mockContext: Context
    private lateinit var mockResources: Resources
    private lateinit var mockWhitelistingUtils: WhitelistingUtils

    private lateinit var model: SettingsViewModel

    @Before
    fun setUp() {
        mockContext = Mockito.mock(Context::class.java)
        mockResources = Mockito.mock(Resources::class.java)
        mockWhitelistingUtils = Mockito.mock(WhitelistingUtils::class.java)
        Mockito.`when`(mockContext.resources).thenReturn(mockResources)

        mockUserActivityDao = UserActivityDaoMockImpl(ArrayList())
        model = SettingsViewModel(mockUserActivityDao, mockWhitelistingUtils)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertNull(model.detailFragment.value)
        Assert.assertFalse(model.showLogoutConformation.value!!)
        Assert.assertFalse(model.showWhitelistDialog.value!!)
        Assert.assertFalse(model.openSyncSettings.value!!)
        Assert.assertFalse(model.openDailyReview.value!!)
        Assert.assertFalse(model.openDndSettings.value!!)
        Assert.assertFalse(model.openInstructions.value!!)
        Assert.assertFalse(model.openNotificationSettings.value!!)
        Assert.assertFalse(model.openPrivacyPolicy.value!!)
        Assert.assertTrue(model.settingsItems.value!!.isEmpty())
    }

    @Test
    @Throws(Exception::class)
    fun checkSettingsCard() {
        val settingsCard = model.getSettingsCard()

        Assert.assertEquals(R.string.title_activity_settings, settingsCard.titleRes)
        Assert.assertEquals(4, settingsCard.items.size)
    }

    @Test
    @Throws(Exception::class)
    fun checkSyncSettingsItem() {
        val syncItem = model.getSettingsCard().items[0] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_sync, syncItem.textRes)
        Assert.assertEquals(R.drawable.ic_sync, syncItem.iconRes)
        Assert.assertNotNull(syncItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openSyncSettings.value!!)
        syncItem.onClickAction.onClick()
        Assert.assertTrue(model.openSyncSettings.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkDndSettingsItem() {
        val dndItem = model.getSettingsCard().items[1] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_dnd, dndItem.textRes)
        Assert.assertEquals(R.drawable.ic_dnd_on, dndItem.iconRes)
        Assert.assertNotNull(dndItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openDndSettings.value!!)
        dndItem.onClickAction.onClick()
        Assert.assertTrue(model.openDndSettings.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkDailyReviewSettingsItem() {
        val dailyReviewItem = model.getSettingsCard().items[2] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_daily_review, dailyReviewItem.textRes)
        Assert.assertEquals(R.drawable.ic_daily_review_notification, dailyReviewItem.iconRes)
        Assert.assertNotNull(dailyReviewItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openDailyReview.value!!)
        dailyReviewItem.onClickAction.onClick()
        Assert.assertTrue(model.openDailyReview.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkNotificationsSettingsItem() {
        val dailyReviewItem = model.getSettingsCard().items[3] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_notifications, dailyReviewItem.textRes)
        Assert.assertEquals(R.drawable.ic_notifications_bell, dailyReviewItem.iconRes)
        Assert.assertNotNull(dailyReviewItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openNotificationSettings.value!!)
        dailyReviewItem.onClickAction.onClick()
        Assert.assertTrue(model.openNotificationSettings.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkLogoutCard() {
        Mockito.`when`(mockResources.getColor(anyInt())).thenReturn(Color.RED)

        val logoutCard = model.getLogoutCard(mockContext)
        Assert.assertEquals(logoutCard.cardColor, Color.RED)

        //Logout item from the card
        val logoutItem = logoutCard.items[0] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_logout, logoutItem.textRes)
        Assert.assertEquals(R.drawable.ic_logout, logoutItem.iconRes)
        Assert.assertNotNull(logoutItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.showLogoutConformation.value!!)
        logoutItem.onClickAction.onClick()
        Assert.assertTrue(model.showLogoutConformation.value!!)

    }

    @Test
    @Throws(Exception::class)
    fun checkWhitelistCard() {
        Mockito.`when`(mockResources.getColor(anyInt())).thenReturn(Color.RED)

        val whitelistCard = model.getWhiteListCard(mockContext)
        Assert.assertEquals(whitelistCard.cardColor, Color.RED)

        //Logout item from the card
        val logoutItem = whitelistCard.items[0] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_white_list, logoutItem.textRes)
        Assert.assertEquals(R.string.settings_subtitle_white_list, logoutItem.subTextRes)
        Assert.assertEquals(R.drawable.ic_battery_status, logoutItem.iconRes)
        Assert.assertNotNull(logoutItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.showWhitelistDialog.value!!)
        logoutItem.onClickAction.onClick()
        Assert.assertTrue(model.showWhitelistDialog.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkHelpCard() {
        val whitelistCard = model.getHelpCard()
        Assert.assertEquals(R.string.title_card_help, whitelistCard.titleRes)
        Assert.assertEquals(2, whitelistCard.items.size)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrivacyPolicyItem() {
        val dailyReviewItem = model.getHelpCard().items[1] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_privacy, dailyReviewItem.textRes)
        Assert.assertEquals(R.drawable.ic_privacy, dailyReviewItem.iconRes)
        Assert.assertNotNull(dailyReviewItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openPrivacyPolicy.value!!)
        dailyReviewItem.onClickAction.onClick()
        Assert.assertTrue(model.openPrivacyPolicy.value!!)
    }

    @Test
    @Throws(Exception::class)
    fun checkInstructionItem() {
        val dailyReviewItem = model.getHelpCard().items[0] as MaterialAboutActionItem
        Assert.assertEquals(R.string.settings_name_instructions, dailyReviewItem.textRes)
        Assert.assertEquals(R.drawable.ic_instruction, dailyReviewItem.iconRes)
        Assert.assertNotNull(dailyReviewItem.onClickAction)

        //Check call click
        Assert.assertFalse(model.openInstructions.value!!)
        dailyReviewItem.onClickAction.onClick()
        Assert.assertTrue(model.openInstructions.value!!)
    }


    @Test
    @Throws(Exception::class)
    fun checkAddWhiteListCard_WhiteListNotAllowed() {
        val testSubscriber = TestObserver<Boolean>()
        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(false)

        model.addWhiteListAppCard(mockContext, mockUserActivityDao, mockWhitelistingUtils).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertComplete()
                .assertValueCount(1)
                .assertValueAt(0, false)
    }

    @Test
    @Throws(Exception::class)
    fun checkAddWhiteListCard_NoLatestItem() {
        val testSubscriber = TestObserver<Boolean>()
        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(true)

        model.addWhiteListAppCard(mockContext, mockUserActivityDao, mockWhitelistingUtils).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertComplete()
                .assertValueCount(1)
                .assertValueAt(0, true)
    }

    @Test
    @Throws(Exception::class)
    fun checkAddWhiteListCard_RecentLastEvent() {
        mockUserActivityDao.insert(UserActivity(
                remoteId = 0,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = true,
                eventEndTimeMills = System.currentTimeMillis(),
                eventStartTimeMills = System.currentTimeMillis() - 10_000
        ))
        val testSubscriber = TestObserver<Boolean>()
        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(true)

        model.addWhiteListAppCard(mockContext, mockUserActivityDao, mockWhitelistingUtils).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertComplete()
                .assertValueCount(1)
                .assertValueAt(0, false)
    }

    @Test
    @Throws(Exception::class)
    fun checkAddWhiteListCard_LastEventBeforeOneDay() {
        val yesterDayMills = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS
        mockUserActivityDao.nukeTable()
        mockUserActivityDao.insert(UserActivity(

                remoteId = 0,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = true,
                eventEndTimeMills = yesterDayMills - 1_000   /* 1 sec */,
                eventStartTimeMills = yesterDayMills - 10_000   /* 10 sec */
        ))
        val testSubscriber = TestObserver<Boolean>()
        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(true)

        model.addWhiteListAppCard(mockContext, mockUserActivityDao, mockWhitelistingUtils).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertComplete()
                .assertValueCount(1)
                .assertValueAt(0, true)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareList_WithWhiteList_OldLatestItemAvailable() {
        val yesterDayMills = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS
        mockUserActivityDao.insert(UserActivity(
                remoteId = 0,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = true,
                eventEndTimeMills = yesterDayMills,
                eventStartTimeMills = yesterDayMills - 10_000
        ))

        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(true)

        model.prepareSettingsList(mockContext)

        Assert.assertNotNull(model.settingsItems.value)
        Assert.assertEquals(model.settingsItems.value!!.size, 4)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareList_WithWhiteList_LatestItemNotAvailable() {
        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(true)
        mockUserActivityDao.nukeTable()

        model.prepareSettingsList(mockContext)

        Assert.assertNotNull(model.settingsItems.value)
        Assert.assertEquals(model.settingsItems.value!!.size, 4)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareList_WithWhiteListNotAllowed_LatestItemAvailable() {
        val yesterDayMills = System.currentTimeMillis() - TimeUtils.ONE_DAY_MILLISECONDS
        mockUserActivityDao.insert(UserActivity(
                remoteId = 0,
                type = UserActivityType.SITTING.toString().toLowerCase(),
                isSynced = true,
                eventEndTimeMills = yesterDayMills,
                eventStartTimeMills = yesterDayMills - 10_000
        ))

        Mockito.`when`(mockWhitelistingUtils.shouldOpenWhiteListDialog(mockContext)).thenReturn(false)

        model.prepareSettingsList(mockContext)

        Assert.assertNotNull(model.settingsItems.value)
        Assert.assertEquals(model.settingsItems.value!!.size, 3)
    }
}
