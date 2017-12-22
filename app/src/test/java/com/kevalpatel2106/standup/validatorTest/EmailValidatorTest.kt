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
class EmailValidatorTest(private val input: String?, private val expected: Boolean) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(null, false),
                    arrayOf("examplegmail.com", false),
                    arrayOf("example@g.com", false),
                    arrayOf("example@gmail.co.m", false),
                    arrayOf("example@gmail.c", false),
                    arrayOf("example@gmail.com", true),
                    arrayOf("", false)
            )
        }
    }


    @Test
    fun testEmail() {
        assertEquals(expected, Validator.isValidEmail(input))
    }
}