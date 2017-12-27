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
