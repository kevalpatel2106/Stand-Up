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

package com.kevalpatel2106.standup.core.dndManager

import com.kevalpatel2106.common.UserSettingsManager
import com.kevalpatel2106.utils.TimeUtils

/**
 * Created by Keval on 19/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object AutoDndMonitoringHelper {

    @JvmStatic
    fun getAutoDndStartTiming(userSettingsManager: UserSettingsManager): Long {
        return with(userSettingsManager.autoDndStartTime) {

            if (this < System.currentTimeMillis()) {    //Auto dnd start time is already passed.

                //Schedule the job tomorrow.
                return@with TimeUtils.getTommorowsCalender12AM().timeInMillis + this
            } else {    //Auto dnd start time is yet to pass.
                //Schedule the job today itself.
                return@with TimeUtils.getTodaysCalender12AM().timeInMillis + this
            }
        }
    }

    @JvmStatic
    fun getAutoDndEndTiming(userSettingsManager: UserSettingsManager): Long {
        return with(userSettingsManager.autoDndEndTime) {

            if (this < java.lang.System.currentTimeMillis()) {    //Auto dnd start time is already passed.

                //Schedule the job tomorrow.
                return@with com.kevalpatel2106.utils.TimeUtils.getTommorowsCalender12AM().timeInMillis + this
            } else {    //Auto dnd start time is yet to pass.
                //Schedule the job today itself.
                return@with com.kevalpatel2106.utils.TimeUtils.getTodaysCalender12AM().timeInMillis + this
            }
        }
    }
}
