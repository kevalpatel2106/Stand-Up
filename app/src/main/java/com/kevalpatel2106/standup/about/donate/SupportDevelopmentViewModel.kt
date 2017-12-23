package com.kevalpatel2106.standup.about.donate

import android.support.annotation.StringRes
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.standup.R

/**
 * Created by Keval on 23/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(SupportDevelopmentActivity::class)
internal class SupportDevelopmentViewModel {

    @StringRes
    internal fun getDonationLink(@DonationAmount amount: Int): Int = when (amount) {
        2 -> R.string.donate_2_dollar_link
        5 -> R.string.donate_5_dollar_link
        10 -> R.string.donate_10_dollar_link
        20 -> R.string.donate_20_dollar_link
        else -> R.string.donate_2_dollar_link /* We will return 2 dollar link */
    }
}