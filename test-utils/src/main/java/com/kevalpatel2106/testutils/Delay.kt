/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.testutils

import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingPolicies
import android.support.test.espresso.IdlingResource

import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
/**
 * Created by Keval Patel on 05/04/17.

 * @author 'https://github.com/kevalpatel2106'
 */

class Delay
/**
 * Private construction.

 * @param waitingTime Wait time.
 */
private constructor(private val mWaitingTime: Long) : IdlingResource {

    private val mStartTime: Long
    private var mResourceCallback: IdlingResource.ResourceCallback? = null

    init {
        this.mStartTime = System.currentTimeMillis()
    }

    override fun getName(): String {
        return Delay::class.java.name + ":" + mWaitingTime
    }

    override fun isIdleNow(): Boolean {
        val elapsed = System.currentTimeMillis() - mStartTime
        val idle = elapsed >= mWaitingTime
        if (idle) mResourceCallback!!.onTransitionToIdle()
        return idle
    }

    override fun registerIdleTransitionCallback(resourceCallback: IdlingResource.ResourceCallback) {
        this.mResourceCallback = resourceCallback
    }

    companion object {
        private var sTimeIdlingResource: Delay? = null

        /**
         * Register idling resource to delay for given time.

         * @param waitTimeMills Wait time in millisecond.
         */
        fun startDelay(waitTimeMills: Long) {
            // Make sure Espresso does not time out
            IdlingPolicies.setMasterPolicyTimeout(waitTimeMills * 2, TimeUnit.MILLISECONDS)
            IdlingPolicies.setIdlingResourceTimeout(waitTimeMills * 2, TimeUnit.MILLISECONDS)

            sTimeIdlingResource = Delay(waitTimeMills)
            Espresso.registerIdlingResources(sTimeIdlingResource)
        }

        /**
         * Stop idling resource.
         */
        fun stopDelay() {
            if (sTimeIdlingResource != null) Espresso.unregisterIdlingResources(sTimeIdlingResource)
            sTimeIdlingResource = null
        }
    }
}
