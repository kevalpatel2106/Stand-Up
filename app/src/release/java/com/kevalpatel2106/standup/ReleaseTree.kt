package com.kevalpatel2106.standup

import android.util.Log
import com.crashlytics.android.Crashlytics

import timber.log.Timber

/**
 * Created by Keval on 13-11-17.
 *
 * Tree for the release application. This timber tree will print the log for the errors only.
 */

class ReleaseTree : Timber.Tree() {
    /**
     * Write a log message to its destination. Called for all level-specific methods by default.
     *
     * @param priority Log level. See [Log] for constants.
     * @param tag Explicit or inferred tag. May be `null`.
     * @param message Formatted log message. May be `null`, but then `t` will not be.
     * @param t Accompanying exceptions. May be `null`, but then `message` will not be.
     */
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (isLoggable(tag, priority)) {
            Log.println(priority, tag, message)

            //Log to crashalytics
            Crashlytics.log(message)
        }
    }

    /**
     * This method will tell timber if it should print log or not.
     */
    override fun isLoggable(tag: String?, priority: Int): Boolean {
        return when (priority) {
            Log.ERROR -> true
            else -> false
        }
    }
}