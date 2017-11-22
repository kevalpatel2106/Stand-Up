package com.kevalpatel2106.utils

import android.os.Build
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

    @Test
    @Throws(Exception::class)
    fun convertToNano() {
        val timeMills = System.currentTimeMillis()
        assertTrue(Utils.convertToNano(timeMills) == timeMills * 1000000)
    }

    @Test
    @Throws(Exception::class)
    fun convertToMilli() {
        val timeNano = System.currentTimeMillis() * 1000000
        assertTrue(Utils.convertToMilli(timeNano) == timeNano / 1000000)
    }

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