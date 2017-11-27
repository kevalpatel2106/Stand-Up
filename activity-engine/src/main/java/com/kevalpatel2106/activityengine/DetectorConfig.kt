package com.kevalpatel2106.activityengine

/**
 * Created by Keval on 24/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object DetectorConfig {

    /**
     * Shared prefrance key to maintain the state of the activity detection.
     */
    internal const val KEY_IS_DETECTION_ENABLE = "key_is_detection_enable"

    /**
     * Activity detection update interval in milliseconds.
     */
    internal const val DETECTION_INTERVAL = 10000L

    /**
     * Broadcast action when the activity update available.
     */
    const val DETECTION_BROADCAST_ACTION = "com.kevalpatel2106.activityengine.update"
}