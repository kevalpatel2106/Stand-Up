package com.kevalpatel2106.utils.utilsTest

import android.os.Build
import com.kevalpatel2106.utils.Utils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class UtilsTest {

    /**
     * Test for [Utils.getDeviceName].
     */
    @Test
    @Throws(Exception::class)
    fun getDeviceName() {
        assertNotNull(Utils.getDeviceName())
        assertTrue(Utils.getDeviceName().contains("-"))
        assertEquals(Utils.getDeviceName(), String.format("%s-%s", Build.MANUFACTURER, Build.MODEL))
    }
}