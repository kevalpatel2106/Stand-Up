package com.kevalpatel2106.utils.timeUtilsTest

import com.kevalpatel2106.utils.TimeUtils
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class TimeUtilsTest {

    @Test
    @Throws(Exception::class)
    fun convertToNano() {
        val timeMills = System.currentTimeMillis()
        assertTrue(TimeUtils.convertToNano(timeMills) == timeMills * 1000000)
    }

    @Test
    @Throws(Exception::class)
    fun convertToMilli() {
        val timeNano = System.currentTimeMillis() * 1000000
        assertTrue(TimeUtils.convertToMilli(timeNano) == timeNano / 1000000)
    }
}