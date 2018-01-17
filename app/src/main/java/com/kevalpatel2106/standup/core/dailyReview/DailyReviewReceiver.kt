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

package com.kevalpatel2106.standup.core.dailyReview

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

/**
 * Created by Keval on 17/01/18.
 * A receiver to get callback whenever the daily review alarm goes off. This receiver will generate
 * the daily review notification and fire it.
 *
 * Due to the background limitations in Android O, we can not let this function run more than 10 secs.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class DailyReviewReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        Timber.i("Displaying the daily review notification.")
    }

}