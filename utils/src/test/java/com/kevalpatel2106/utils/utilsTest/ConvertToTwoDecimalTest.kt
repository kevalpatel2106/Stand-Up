package com.kevalpatel2106.utils.utilsTest

import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Parameterized::class)
class ConvertToTwoDecimalTest(private val input: Double,
                              private val expected: Double) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(1, 1.0),
                    arrayOf(1.12, 1.12),
                    arrayOf(1.125, 1.13),
                    arrayOf(1.122, 1.12),
                    arrayOf(1.127, 1.13),
                    arrayOf(1.10, 1.10),

                    //Negative number
                    arrayOf(-1, -1.0),
                    arrayOf(-1.12, -1.12),
                    arrayOf(-1.125, -1.13),
                    arrayOf(-1.122, -1.12),
                    arrayOf(-1.127, -1.13),
                    arrayOf(-1.10, -1.10),

                    arrayOf(0, 0)
            )
        }
    }

    @Test
    @Throws(IOException::class)
    fun testConvertToTwoDecimal() {
        Assert.assertEquals(expected, Utils.convertToTwoDecimal(input), 0.0)
    }
}