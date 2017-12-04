package com.kevalpatel2106.standup

import com.kevalpatel2106.standup.constants.AppConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by Kevalpatel2106 on 04-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Parameterized::class)
class WeightValidatorTest(private val input: Float, private val expected: Boolean) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(0F, false),
                    arrayOf(0.1F, false),
                    arrayOf(AppConfig.MIN_WEIGHT, true),
                    arrayOf(AppConfig.MAX_WEIGHT, true),
                    arrayOf((AppConfig.MAX_WEIGHT + AppConfig.MIN_WEIGHT) / 2, true),
                    arrayOf(AppConfig.MIN_WEIGHT - 2F, false),
                    arrayOf(AppConfig.MAX_WEIGHT + 2F, false)
            )
        }
    }

    @Test
    fun testEmail() {
        assertEquals(expected, Validator.isValidWeight(input))
    }
}