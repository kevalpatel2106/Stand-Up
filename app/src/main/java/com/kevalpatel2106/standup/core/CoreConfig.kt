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

package com.kevalpatel2106.standup.core

import com.kevalpatel2106.standup.BuildConfig

/**
 * Created by Keval on 24/11/17.
 * Configuration file for the code package.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object CoreConfig {

    //******* Activity monitoring
    /**
     * Minimum threshold required for the [com.google.android.gms.location.DetectedActivity] for
     * processing the data. Tha means iff the confidance level is below 30% we will ignore that event.
     */
    internal const val CONFIDENCE_THRESHOLD = 30

    internal const val MONITOR_SERVICE_PERIOD = 45  //sec

    internal const val MONITOR_SERVICE_PERIOD_TOLERANCE = 5  //sec

    //******* Reminder notifications
    /**
     * Interval between two "Stand up now" notifications in seconds.
     */
    internal val STAND_UP_REMINDER_INTERVAL = if (BuildConfig.DEBUG) 5 * 60 else 60 * 60 /* 60 min */   //secs

    internal const val NOTIFICATION_SERVICE_PERIOD_TOLERANCE = 60  //sec


    //******* Syncing
    internal const val SYNC_SERVICE_PERIOD_TOLERANCE = 60  //sec

    internal const val TAG_RX_SYNC_STARTED = "rx_sync_started"

    internal const val TAG_RX_SYNC_ENDED = "rx_sync_ended"
}
