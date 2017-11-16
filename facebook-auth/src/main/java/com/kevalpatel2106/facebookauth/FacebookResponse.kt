package com.kevalpatel2106.facebookauth

/**
 * Created by multidots on 6/16/2016.
 * This callback will notify about the facebook authentication status.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */
interface FacebookResponse {

    /**
     * This callback will be available when facebook sign in call fails.
     */
    fun onFbSignInFail()

    /**
     * This method will be called whenever [FacebookHelper], authenticate and get the profile
     * detail from the facebook.
     *
     * @param facebookUser [FacebookUser].
     */
    fun onFbProfileReceived(facebookUser: FacebookUser)

    /**
     * This callback will be available whenever facebook signout call completes. No matter success of
     * failure.
     */
    fun onFBSignOut()
}
