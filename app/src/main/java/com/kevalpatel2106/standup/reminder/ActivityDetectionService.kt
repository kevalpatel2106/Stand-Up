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

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.support.annotation.VisibleForTesting
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivity
import com.kevalpatel2106.standup.db.userActivity.UserActivityHelper
import com.kevalpatel2106.standup.db.userActivity.UserActivityType
import com.kevalpatel2106.standup.reminder.repo.ReminderRepo
import com.kevalpatel2106.standup.reminder.repo.ReminderRepoImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*


/**
 * Created by Keval on 25/11/17.
 * This is receiver will receive the updates when the [UserActivity] is changed. [ReminderScheduler]
 * will broadcast intent with action [ReminderConfig.DETECTION_BROADCAST_ACTION] to invoke this
 * receiver.
 *
 * The [Intent] contains list of all the detected activities and their confidence level.
 * This data can be parsed using [ActivityRecognitionResult.extractResult].
 *
 * Once this list of probable user activity is available, it will detect weather the user is sitting
 * or moving/standing based on the [DetectedActivity]. If the user is sitting [onUserSitting] will
 * be called. Iff the user is moving [onUserMoving] will be called.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see DetectedActivity
 */
class ActivityDetectionService : IntentService(ActivityDetectionService::class.java.name) {

    @VisibleForTesting
    internal var mReminderRepo: ReminderRepo = ReminderRepoImpl()

    @SuppressLint("VisibleForTests")
    override fun onHandleIntent(intent: Intent) {
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList
        Timber.d("Detected Activities: ".plus(detectedActivities.toString()))

        if (ActivityDetectionHelper.shouldIgnoreThisEvent(detectedActivities)) {
            //Not enough confidence
            //Let's ignore this event.
            return
        }

        if (ActivityDetectionHelper.isUserSitting(detectedActivities)) {
            //User is sitting
            onUserSitting(mReminderRepo)
        } else {
            //User is moving
            onUserMoving(mReminderRepo)
        }
    }

    private fun onUserMoving(repo: ReminderRepo) {
        Timber.d("User is MOVING.")

        //Push the notification back to 1 hour
        //TODO Schedule next reminder

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.MOVING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun onUserSitting(repo: ReminderRepo) {
        Timber.d("User is SITTING.")

        //Add the new value to database.
        repo.insertNewAndTerminatePreviousActivity(UserActivityHelper
                .createLocalUserActivity(UserActivityType.SITTING))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
}