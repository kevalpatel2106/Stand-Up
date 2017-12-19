package com.kevalpatel2106.standup.notification

import com.google.firebase.iid.FirebaseInstanceIdService
import com.kevalpatel2106.standup.authentication.deviceReg.RegisterDeviceService
import com.kevalpatel2106.utils.UserSessionManager

/**
 * Created by Keval on 01-Jun-17.
 * This service handles the instance id refresh.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see FirebaseInstanceIdService
 */

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()

        //Start syncing the new token
        if (UserSessionManager.isUserLoggedIn) RegisterDeviceService.start(this)
    }

}
