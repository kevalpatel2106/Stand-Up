package com.kevalpatel2106.standup.fcm

import android.app.Activity
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.By
import android.support.test.uiautomator.UiDevice
import android.support.test.uiautomator.UiSelector
import android.support.test.uiautomator.Until
import android.support.v4.app.NotificationCompat
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.authentication.verification.EmailVerifiedNotification
import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Kevalpatel2106 on 06-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class TestNotificationTestStandUpNow : BaseTestClass() {
    override fun getActivity(): Activity? = null

    private lateinit var mDevice: UiDevice

    @Before
    fun init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        //Open notification drawer
        mDevice.openNotification()
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000)
    }

    @Test
    fun checkBuildNotification() {
        val builder = EmailVerifiedNotification.buildNotification(InstrumentationRegistry.getContext(),
                "This is test message.")

        assertEquals(builder.priority, NotificationCompat.PRIORITY_HIGH)
        assertEquals(builder.tickerText, "This is test message.")
        assertEquals(builder.channelId, NotificationChannelType.ACCOUNT_NOTIFICATION_CHANNEL)
    }

    @Test
    fun checkNotificationDisplayed() {
        EmailVerifiedNotification.notify(InstrumentationRegistry.getContext().applicationContext,
                "This is test message.")

        /*
         * access Notification Center through resource id, package name, class name.
         *
         * if you want to check resource id, package name or class name of the specific view
         * in the screen, run 'uiautomatorviewer' from command.
         */
        val notificationStackScroller = UiSelector()
                .packageName("com.android.systemui")
                .className("android.view.ViewGroup")
                .resourceId("com.android.systemui:id/notification_stack_scroller")
        val notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller)
        assertTrue(notificationStackScrollerUiObject.exists())

        /*
         * access top notification in the center through parent
         */
        val notiSelectorUiObject = notificationStackScrollerUiObject.getChild(UiSelector()
                .text(InstrumentationRegistry
                        .getTargetContext()
                        .getString(R.string.application_name)))
        assertTrue(notiSelectorUiObject.exists())
    }

}