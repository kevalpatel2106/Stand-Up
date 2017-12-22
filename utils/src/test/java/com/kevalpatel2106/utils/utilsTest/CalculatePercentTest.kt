package com.kevalpatel2106.utils.utilsTest

import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 22-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(Enclosed::class)
class CalculatePercentTest {

    @RunWith(Parameterized::class)
    class CalculatePercentParametrizedTest(private val value: Long,
                                           private val total: Long,
                                           private val expected: Double) {

        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(0, 100, 0.0),
                        arrayOf(10, 100, 10.00),
                        arrayOf(100, 100, 100.0),
                        arrayOf(110, 100, 110.0)
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testCalculatePercent() {
            Assert.assertEquals(expected, Utils.calculatePercent(value, total), 0.0)
        }
    }

    @RunWith(JUnit4::class)
    class CalculatePercentNotParametrizedTest {

        @Test
        @Throws(IOException::class)
        fun testZeroTotalValue() {
            try {
                Utils.calculatePercent(10, 0)
                Assert.fail("Total amount cannot be 0. Divide by zero.")
            } catch (e: IllegalArgumentException) {
                //Test Passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testNegativeTotalValue() {
            try {
                Utils.calculatePercent(10, -100)
                Assert.fail("Total amount cannot be negative.")
            } catch (e: IllegalArgumentException) {
                //Test Passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testNegativeValue() {
            try {
                Utils.calculatePercent(-10, 100)
                Assert.fail("Value cannot be negative.")
            } catch (e: IllegalArgumentException) {
                //Test Passed
            }
        }
    }
}