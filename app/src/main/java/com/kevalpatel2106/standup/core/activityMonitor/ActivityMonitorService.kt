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

import android.content.Context
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.core.CoreConfig
import com.kevalpatel2106.standup.core.di.DaggerCoreComponent
import com.kevalpatel2106.standup.core.reminder.NotificationSchedulerService
import com.kevalpatel2106.standup.core.repo.CoreRepo
import com.kevalpatel2106.standup.misc.UserSessionManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Keval on 30/12/17.
 * This is [JobService] runs periodically after [CoreConfig.MONITOR_SERVICE_PERIOD] seconds. This
 * service is responsible for detecting and monitoring the user activity.
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
 * If the user is sitting service will not reschedule the [NotificationSchedulerService]. But if the
 * user is moving currently (that means user has stretched his/het legs :-)) it will push back the
 * [NotificationSchedulerService] by [CoreConfig.STAND_UP_REMINDER_INTERVAL] seconds and reschedule
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
class ActivityMonitorService : JobService(), OnSuccessListener<DetectedActivityResponse> {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    @Inject lateinit var coreRepo: CoreRepo
    @Inject lateinit var userSessionManager: UserSessionManager
    @Inject lateinit var sharedPrefsProvider: SharedPrefsProvider

    companion object {

        @JvmStatic
        internal fun scheduleMonitoringJob(context: Context) {
            //Schedule the jobParams
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .mustSchedule(ActivityMonitorHelper.prepareJob(context))
        }

        @JvmStatic
        internal fun cancel(context: Context) {
            FirebaseJobDispatcher(GooglePlayDriver(context))
                    .cancel(ActivityMonitorHelper.ACTIVITY_MONITOR_JOB_TAG)

            //Stop the notifications
            NotificationSchedulerService.cancel(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        DaggerCoreComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@ActivityMonitorService)
    }

    /**
     * [JobParameters] received in [onStartJob].
     */
    private lateinit var jobParams: JobParameters

    override fun onStopJob(job: JobParameters?): Boolean {
        return true    //Job done. Do not retry it.
    }

    /**
     * Whenever job starts [onStartJob]  will be called. This will check if the activity should
     * get the list of [DetectedActivity] based on [ActivityMonitorHelper.shouldMonitoringActivity].
     *
     * If [ActivityMonitorHelper.shouldMonitoringActivity] returns true, using [Awareness API](https://developers.google.com/awareness/)
     * it will try to detect current user activity and invoke [onSuccess]. If the activity detection
     * fails, method will terminate current job.
     *
     * If [ActivityMonitorHelper.shouldMonitoringActivity] returns false, it will terminate all the
     * upcoming scheduled jobs.
     *
     * @see onSuccess
     */
    override fun onStartJob(job: JobParameters): Boolean {

        //Check if service should be monitoring user activities?
        if (ActivityMonitorHelper.shouldMonitoringActivity(userSessionManager)) {
            jobParams = job

            //Use the snapshot api to get result for the user activity.
            Awareness.getSnapshotClient(this).detectedActivity
                    .addOnSuccessListener(this@ActivityMonitorService)
                    .addOnFailureListener {
                        //Error occurred
                        Timber.e(it.message)
                        finishJob(job)
                    }
        } else {

            //This shouldn't happen. Cancel all the upcoming jobs.
            cancel(this)
            return false
        }
        return true    //Job done. Wait for the jobParams to finish
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun finishJob(job: JobParameters) {
        //Mark jobParams as finished
        jobFinished(job, false /* Job done. Do not retry it. */)
    }

    override fun onSuccess(activityResponse: DetectedActivityResponse) {
        Timber.i("Detected Activities: ".plus(activityResponse.activityRecognitionResult.probableActivities.toString()))

        val userActivity = ActivityMonitorHelper.convertToUserActivity(ArrayList(activityResponse
                .activityRecognitionResult.probableActivities))

        //Finish the job if the activity can ignored.
        if (userActivity == null) {
            finishJob(jobParams)
            return
        }

        if (ActivityMonitorHelper.shouldScheduleNotification(userActivity, sharedPrefsProvider)) {
            NotificationSchedulerService.cancel(this@ActivityMonitorService)
            NotificationSchedulerService.scheduleNotification(this@ActivityMonitorService)
        }

        //Add the new value to database.
        coreRepo.insertNewAndTerminatePreviousActivity(userActivity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { compositeDisposable.add(it) }
                .doAfterTerminate { finishJob(jobParams) }
                .subscribe({
                    //NO OP
                }, {
                    //Error.
                    //NO OP
                    Timber.e(it.message)
                })
    }
}