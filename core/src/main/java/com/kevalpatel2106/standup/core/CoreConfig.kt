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

import java.util.concurrent.TimeUnit

/**
 * Created by Keval on 24/11/17.
 * Configuration file for the code package.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object CoreConfig {

    //******* Activity monitoring
    /**
     * Minimum threshold required for the [com.google.android.gms.location.DetectedActivity] for
     * processing the data. Tha means iff the confidence level is below 30% we will ignore that event.
     * Value : 30%
     */
    internal const val CONFIDENCE_THRESHOLD = 30

    /**
     * Interval duration for monitoring the user activity.
     * Value: 45 Seconds
     */
    internal val MONITOR_SERVICE_PERIOD = TimeUnit.SECONDS.toMillis(45)  //45 seconds

    //******* Reminder notifications
    /**
     * Interval between two "Stand up now" notifications in seconds.
     * Value: 1 Hour
     */
    internal val STAND_UP_REMINDER_INTERVAL = if (BuildConfig.DEBUG) {
        TimeUnit.MINUTES.toMillis(15)    /* 15 min */
    } else {
        TimeUnit.MINUTES.toMillis(60)   /* 60 min */
    }

    /**
     * Flex timing/tolerance for [STAND_UP_REMINDER_INTERVAL].
     * Value : 5 Minutes
     */
    internal val NOTIFICATION_SERVICE_PERIOD_TOLERANCE = TimeUnit.MINUTES.toMillis(5)  //5 min


    //******* Syncing
    /**
     * Flex timing/tolerance for sync job.
     * Value : 5 Minutes
     */
    internal val SYNC_SERVICE_PERIOD_TOLERANCE = TimeUnit.MINUTES.toMillis(5)   //5 min

    /**
     * Tag for the rx event. This event will broadcast when sync job starts.
     *
     * @see com.kevalpatel2106.standup.core.sync.SyncJob.notifySyncStarted
     */
    const val TAG_RX_SYNC_STARTED = "rx_sync_started"

    /**
     * Tag for the rx event. This event will broadcast when sync job ends.
     *
     * @see com.kevalpatel2106.standup.core.sync.SyncJob.notifySyncTerminated
     */
    const val TAG_RX_SYNC_ENDED = "rx_sync_ended"
}
