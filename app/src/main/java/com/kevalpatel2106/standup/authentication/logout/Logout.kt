package com.kevalpatel2106.standup.authentication.logout

import android.content.Context
import android.widget.Toast
import com.kevalpatel2106.activityengine.ActivityDetector
import com.kevalpatel2106.network.consumer.NWErrorConsumer
import com.kevalpatel2106.network.consumer.NWSuccessConsumer
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.standup.authentication.repo.LoginResponseData
import com.kevalpatel2106.standup.authentication.repo.LogoutRequest
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.constants.SharedPreferenceKeys
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.Utils

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
            .subscribe(object : NWSuccessConsumer<LoginResponseData>() {

                override fun onSuccess(data: LoginResponseData?) {
                    //Clear all the data.
                    clearSession(context)
                }
            }, object : NWErrorConsumer() {

                override fun onInternetUnavailable(message: String) {
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                }

                override fun onError(code: Int, message: String) {
                    //Even though there is error, we will treat it as the success response and clear
                    //all user data.
                    //Clear all the data.
                    clearSession(context)
                }
            })
}