package com.kevalpatel2106.utils

import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert.*
import android.app.Activity
import android.provider.Settings
import android.support.test.InstrumentationRegistry
import org.junit.Test


/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UtilsUiTest : BaseTestClass() {
    override fun getActivity(): Activity? = null

    @Test
    @Throws(Exception::class)
    fun getDeviceId() {
        assertNotNull(Utils.getDeviceId(InstrumentationRegistry.getTargetContext()))
        assertEquals(Utils.getDeviceId(InstrumentationRegistry.getTargetContext()),
                Settings.Secure.getString(InstrumentationRegistry.getContext().contentResolver,
                        Settings.Secure.ANDROID_ID))
    }
}