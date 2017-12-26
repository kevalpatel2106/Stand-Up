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
class DateValidatorTest(private val input: Int, private val expected: Boolean) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            val list = ArrayList<Array<out Any?>>()
            (1..31).mapTo(list) { arrayOf(it, true) }
            list.add(arrayOf(0, false))
            list.add(arrayOf(32, false))
            list.add(arrayOf(-1, false))
            return list
        }
    }

    @Test
    fun testIsValidDate() {
        assertEquals(expected, Validator.isValidDate(input))
    }
}