package com.kevalpatel2106.standup.about.donate

import com.kevalpatel2106.standup.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Keval on 23/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Parameterized::class)
class SupportDevelopmentViewModelTest(private val amount: Int,
                                      private val link: Int) {

    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(2, R.string.donate_2_dollar_link),
                    arrayOf(5, R.string.donate_5_dollar_link),
                    arrayOf(10, R.string.donate_10_dollar_link),
                    arrayOf(20, R.string.donate_20_dollar_link),
                    arrayOf(15, R.string.donate_2_dollar_link)
            )
        }
    }

    private lateinit var model: SupportDevelopmentViewModel

    @Before
    fun setUp() {
        model = SupportDevelopmentViewModel()
    }

    @Test
    @Throws(IOException::class)
    fun checkGetDonationLink() {
        Assert.assertEquals(model.getDonationLink(amount), link)
    }
}