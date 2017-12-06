package com.kevalpatel2106.standup.authentication.logout

import android.content.Context
import com.kevalpatel2106.activityengine.ActivityDetector
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.repo.LogoutRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
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
     * Clear the shared prefrance and jobs
     */
    internal fun clearSession(context: Context) {
        //Stop the device registration service.
        RegisterDeviceService.stop(context)
        SharedPrefsProvider.removePreferences(SharedPreferenceKeys.IS_DEVICE_REGISTERED)

        //Clear user session
        UserSessionManager.clearUserSession()
        UserSessionManager.clearToken()

        //Stop the activity detection
        ActivityDetector.stopDetection()
    }

    /**
     * Make the logout api call and clear the user data on success or failure. In case of internet
     * connectivity issue, we will prevent the logout action.
     *
     * @return Disposable of the api call.
     */
    internal fun logout(context: Context, userAuthRepository: UserAuthRepository) = userAuthRepository
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