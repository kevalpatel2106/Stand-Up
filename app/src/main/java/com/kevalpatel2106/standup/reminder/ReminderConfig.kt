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
    internal const val STAND_UP_DURATION = 60 * 60 /* 60 min */

    /**
     * Minimum threshold required for the user activity for processing the data.
     */
    internal const val CONFIDENCE_THRESHOLD = 30

    internal const val MONITOR_SERVICE_PERIOD = 45  //sec

    internal const val MONITOR_SERVICE_PERIOD_TOLERANCE = 5  //sec

    internal const val NOTIFICATION_SERVICE_PERIOD_TOLERANCE = 60  //sec

    internal const val SYNC_SERVICE_PERIOD_TOLERANCE = 60  //sec

    internal const val TAG_RX_SYNC_STARTED = "rx_sync_started"

    internal const val TAG_RX_SYNC_ENDED = "rx_sync_ended"
}