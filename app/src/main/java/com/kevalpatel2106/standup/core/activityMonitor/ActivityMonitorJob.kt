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

package com.kevalpatel2106.standup.core.activityMonitor

import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.Context
import com.evernote.android.job.JobManager
import com.evernote.android.job.JobRequest
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.core.AsyncJob
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerJob
import com.kevalpatel2106.standup.core.repo.CoreRepo
import com.kevalpatel2106.utils.SharedPrefsProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Keval on 30/12/17.
 * This service is responsible for detecting and monitoring the user activity. This is [JobService]
 * runs every [CoreConfig.MONITOR_SERVICE_PERIOD] seconds. While finishing the the tasks, this job
 * will reschedule it again after [CoreConfig.MONITOR_SERVICE_PERIOD]. We cannot set periodic
 * [JobScheduler] job for such a small duration.
 *
 *
 * This service will retrieve the activities, which user is currently doing using [
 * Awareness API](https://developers.google.com/awareness/). It users user activity snapshot api to
 * retrieve the list of [DetectedActivity]. Service will validate if the list of all probable [DetectedActivity]
 * is valid for processing based on [ActivityMonitorHelper.shouldIgnoreThisEvent]. Basically, it will
 * only consider the [DetectedActivity] with the highest confidence level and the highest confidence
 * should be more than [CoreConfig.CONFIDENCE_THRESHOLD].
 *
 * Once this list of probable user activity is available, it will detect weather the user is sitting
 * or moving/standing based on the [DetectedActivity] using [ActivityMonitorHelper.isUserSitting].
 * If the user is sitting service will not reschedule the [NotificationSchedulerJob]. But if the
 * user is moving currently (that means user has stretched his/het legs :-)) it will push back the
 * [NotificationSchedulerJob] by [CoreConfig.STAND_UP_REMINDER_INTERVAL] seconds and reschedule
 * the service.
 *
 * This service will get the user activity list only if [ActivityMonitorHelper.shouldMonitoringActivity]
 * returns true. Otherwise, service will skip the processing [DetectedActivity] and terminate immediately.
 *
 * @see [Get the current activity](https://developers.google.com/awareness/android-api/snapshot-get-data#get_the_current_activity)
 * @see <a href="https://1drv.ms/u/s!AiLigYLwpLlZk4lQcRMj8EZTxF5o3A">Diagram/Flow</a>
 * @see ActivityMonitorHelper.shouldIgnoreThisEvent
 * @see ActivityMonitorHelper.isUserSitting
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ActivityMonitorJob : AsyncJob(), OnSuccessListener<DetectedActivityResponse> {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    companion object {
        internal const val ACTIVITY_MONITOR_JOB_TAG = "activity_monitor_job_tag"

        @JvmStatic
        internal fun scheduleNextJob(): Boolean {
            synchronized(ActivityMonitorJob::class) {

                //Schedule the job
                val id = JobRequest.Builder(ACTIVITY_MONITOR_JOB_TAG)
                        .setUpdateCurrent(true)
                        .setExact(CoreConfig.MONITOR_SERVICE_PERIOD)
                        .build()
                        .schedule()

                Timber.i("Activity monitoring job with id $id scheduled after ${CoreConfig.MONITOR_SERVICE_PERIOD} milliseconds.")
                return true
            }
        }

        @JvmStatic
        internal fun cancel(context: Context) {
            JobManager.instance().cancelAllForTag(ACTIVITY_MONITOR_JOB_TAG)

            Timber.i("Canceling activity monitoring job.")

            //Stop the notifications
            NotificationSchedulerJob.cancel(context)
        }
    }

    @Inject
    lateinit var coreRepo: CoreRepo

    @Inject
    lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var sharedPrefsProvider: SharedPrefsProvider

    /**
     * Whenever job starts [onRunJobAsync]  will be called. This will check if the activity should
     * get the list of [DetectedActivity] based on [ActivityMonitorHelper.shouldMonitoringActivity].
     *
     * If [ActivityMonitorHelper.shouldMonitoringActivity] returns true, using [Awareness API](https://developers.google.com/awareness/)
     * it will try to detect current user activity and invoke [onSuccess]. If the activity detection
     * fails, method will terminate current job.
     *
     * If [ActivityMonitorHelper.shouldMonitoringActivity] returns false, it will terminate the current
     * job and won't schedule next job.
     *
     * @see onSuccess
     */
    override fun onRunJobAsync(params: Params) {
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@ActivityMonitorJob)

        //Check if service should be monitoring user activities?
        if (ActivityMonitorHelper.shouldMonitoringActivity(userSessionManager)) {

            //Use the snapshot api to get result for the user activity.
            Awareness.getSnapshotClient(context).detectedActivity
                    .addOnSuccessListener(this@ActivityMonitorJob)
                    .addOnFailureListener {
                        //Error occurred
                        Timber.e(it.message)

                        destroyAndScheduleNextJob()
                    }
        } else {
            stopJob(Result.SUCCESS)
        }
    }

    private fun destroyAndScheduleNextJob() {
        compositeDisposable.dispose()
        scheduleNextJob()
        stopJob(Result.SUCCESS)
    }


    override fun onSuccess(activityResponse: DetectedActivityResponse) {
        Timber.i("Detected Activities: ".plus(activityResponse.activityRecognitionResult.probableActivities.toString()))

        val userActivity = ActivityMonitorHelper.convertToUserActivity(ArrayList(activityResponse
                .activityRecognitionResult.probableActivities))

        //Finish the job if the activity can ignored.
        if (userActivity == null) {
            destroyAndScheduleNextJob()
            return
        }

        if (ActivityMonitorHelper.shouldScheduleNotification(userActivity, sharedPrefsProvider)) {
            NotificationSchedulerJob.scheduleNotification(sharedPrefsProvider)
        }

        //Add the new value to database.
        coreRepo.insertNewAndTerminatePreviousActivity(userActivity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { compositeDisposable.add(it) }
                .doAfterTerminate { destroyAndScheduleNextJob() }
                .subscribe({
                    //NO OP
                }, {
                    //Error.
                    //NO OP
                    Timber.e(it.message)
                })
    }
}
