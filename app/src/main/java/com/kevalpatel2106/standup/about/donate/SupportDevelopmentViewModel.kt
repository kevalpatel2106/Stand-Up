/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.kevalpatel2106.standup.about.donate

import android.annotation.SuppressLint
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

    @SuppressLint("SwitchIntDef")
    @StringRes
    internal fun getDonationLink(@DonationAmount amount: Int): Int = when (amount) {
        2 -> R.string.donate_2_dollar_link
        5 -> R.string.donate_5_dollar_link
        10 -> R.string.donate_10_dollar_link
        20 -> R.string.donate_20_dollar_link
        else -> R.string.donate_2_dollar_link /* We will return 2 dollar link */
    }
}