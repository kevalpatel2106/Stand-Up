package com.kevalpatel2106.standup.authentication.logout

import android.app.NotificationManager
import android.content.Context
import com.kevalpatel2106.standup.SplashActivity
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.repo.LogoutRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepositoryImpl
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.standup.engine.Engine
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 * Methods to logout the user.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object Logout {

    /**
     * Clear the shared preference and jobs
     */
    internal fun clearSession(context: Context) {
        //Stop the device registration service.
        RegisterDeviceService.stop(context)

        //Clear shared prefs
        SharedPrefsProvider.removePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)
        SharedPrefsProvider.removePreferences(SharedPreferenceKeys.IS_NAVIGATION_DRAWER_DISPLAYED)

        //Log analytics
        context.logEvent(AnalyticsEvents.EVENT_LOGOUT)

        //Clear user session
        UserSessionManager.clearUserSession()
        UserSessionManager.clearToken()

        //Stop the activity detection
        Engine.shutDown(context.applicationContext)

        //Clear all the notifications
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancelAll()

        //Launch splash
        val splashIntent = SplashActivity.getLaunchIntent(context)
        context.startActivity(splashIntent)
    }

    /**
     * Make the logout api call and clear the user data on success or failure. In case of internet
     * connectivity issue, we will prevent the logout action.
     *
     * @return Disposable of the api call.
     */
    @JvmOverloads
    internal fun logout(context: Context,
                        userAuthRepository: UserAuthRepository = UserAuthRepositoryImpl()) = userAuthRepository
            .logout(LogoutRequest(UserSessionManager.userId, Utils.getDeviceId(context)))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //Clear all the data.
                clearSession(context)
            }, {
                //Even though there is error, we will treat it as the success response and clear
                //all user data.
                //Clear all the data.
                clearSession(context)
            })
}