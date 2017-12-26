package com.kevalpatel2106.standup.validatorTest

import com.kevalpatel2106.standup.Validator
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
class MonthValidatorTest(private val input: Int, private val expected: Boolean) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            val list = ArrayList<Array<out Any?>>()
            (0..11).mapTo(list) { arrayOf(it, true) }
            list.add(arrayOf(12, false))
            list.add(arrayOf(-1, false))
            return list
        }
    }

    @Test
    fun testIsValidMonth() {
        assertEquals(expected, Validator.isValidMonth(input))
    }
}