package com.kevalpatel2106.standup.userActivity.detector

/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object DetectorConfig {

    /**
     * Interval between two "Stand up now" notifications in seconds.
     */
    internal const val STAND_UP_DURATION = 10 * 60 /*1 hour*/

    /**
     * Tag for the [StandUpNotifier] job.
     */
    internal const val SCHEDULER_JOB_TAG = "tag"

    /**
     * Shared prefrance key to maintain the state of the activity detection.
     */
    internal const val KEY_IS_DETECTION_ENABLE = "key_is_detection_enable"

    /**
     * Activity detection update interval in milliseconds.
     */
    internal const val DETECTION_INTERVAL = 30000L /*30 secs*/

    /**
     * Broadcast action when the activity update available.
     */
    internal const val DETECTION_BROADCAST_ACTION = "com.kevalpatel2106.standup.userActivity.engine.activityUpdate"
}