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

package com.kevalpatel2106.standup.authentication.logout

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.kevalpatel2106.standup.application.SplashActivity
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.repo.LogoutRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.db.userActivity.UserActivityDao
import com.kevalpatel2106.standup.reminder.activityMonitor.ActivityMonitorService
import com.kevalpatel2106.standup.reminder.notification.NotificationSchedulerService
import com.kevalpatel2106.standup.reminder.sync.SyncService
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 * Methods to logout the user.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class Logout constructor(private val application: Application,
                         private val sharedPrefsProvider: SharedPrefsProvider,
                         private val userSessionManager: UserSessionManager,
                         private val userAuthRepository: UserAuthRepository,
                         private val userActivityDao: UserActivityDao) {

    /**
     * Clear the shared preference and jobs
     */
    internal fun clearSession() {
        //Stop the device registration service.
        RegisterDeviceService.stop(application)

        //Clear shared prefs
        sharedPrefsProvider.removePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)
        sharedPrefsProvider.removePreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED)

        //Clear user session
        userSessionManager.clearUserSession()
        userSessionManager.clearToken()

        //Nuke the table
        Completable.create({ userActivityDao.nukeTable() })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe()

        //Cancel all the jobs
        NotificationSchedulerService.cancel(application)
        ActivityMonitorService.cancel(application)
        SyncService.cancel(application)

        //Clear all the notifications
        val manager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()

        //Launch splash
        val splashIntent = SplashActivity.getLaunchIntent(application)
        application.startActivity(splashIntent)

        //Log analytics
        application.logEvent(AnalyticsEvents.EVENT_LOGOUT)
    }

    /**
     * Make the logout api call and clear the user data on success or failure. In case of internet
     * connectivity issue, we will prevent the logout action.
     *
     * @return Disposable of the api call.
     */
    internal fun logout(): Disposable {
        return userAuthRepository
                .logout(LogoutRequest(userSessionManager.userId, Utils.getDeviceId(application)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    //Clear all the data.
                    clearSession()
                }, {
                    //Even though there is error, we will treat it as the success response and clear
                    //all user data.
                    //Clear all the data.
                    clearSession()
                })
    }
}