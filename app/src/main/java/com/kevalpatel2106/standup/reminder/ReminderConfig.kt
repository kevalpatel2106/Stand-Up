/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.reminder

/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object ReminderConfig {

    /**
     * Interval between two "Stand up now" notifications in seconds.
     */
    internal const val STAND_UP_DURATION = 10 * 60 /*1 hour*/

    /**
     * Tag for the [ReminderNotifyService] job.
     */
    internal const val SCHEDULER_JOB_TAG = "tag"

    /**
     * For internal use only.
     */
    internal const val ENABLE_ACTIVITY_DETECTION = true

    /**
     * Activity detection update interval in milliseconds.
     */
    internal const val DETECTION_INTERVAL = 30000L /*30 secs*/

    /**
     * Broadcast action when the activity update available.
     */
    internal const val DETECTION_BROADCAST_ACTION = "com.kevalpatel2106.standup.reminder.activityUpdate"

    /**
     * Minimum threshold required for the user activity for processing the data.
     */
    internal const val CONFIDENCE_THRESHOLD = 30
}