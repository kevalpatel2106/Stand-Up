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

    internal const val CONFIDENCE_THRESHOLD = 30
}