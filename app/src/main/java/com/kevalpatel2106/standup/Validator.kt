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

package com.kevalpatel2106.standup

import com.kevalpatel2106.standup.constants.AppConfig
import java.util.regex.Pattern

/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object Validator {

    //Email pattern regex.
    private val EMAIL_PATTERN = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")

    /**
     * Validate email address.
     * Regex pattern : [.EMAIL_PATTERN].
     *
     * @param email email to validate.
     * @return true if the email is valid.
     */
    fun isValidEmail(email: String?): Boolean {
        return email != null && !email.isEmpty() && Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
    }

    /**
     * Validate the password. The password should not contain all the spaces. Length should be
     * grater than [AppConfig.MIN_PASSWORD] and less than or equal to [AppConfig.MAX_PASSWORD].
     *
     * @param password password to check
     * @return true if the password is valid.
     */
    fun isValidPassword(password: String?): Boolean {
        return password != null
                && !password.isEmpty()
                && password.length > AppConfig.MIN_PASSWORD
                && password.length <= AppConfig.MAX_PASSWORD
    }

    /**
     * Validate the user display name. Length should be
     * grater than [AppConfig.MIN_NAME] and less than or equal to [AppConfig.MAX_NAME].
     *
     * @param name name of the user to validate.
     * @return true if the name is valid.
     */
    fun isValidName(name: String?): Boolean {
        return name != null
                && !name.isEmpty()
                && name.length > AppConfig.MIN_NAME
                && name.length <= AppConfig.MAX_NAME
    }

    /**
     * Check weather [height] is valid or not. Valid [height] must be [AppConfig.MIN_HEIGHT] and
     * [AppConfig.MAX_HEIGHT].
     *
     * @return True if the [height] is valid.
     */
    fun isValidHeight(height: Float) = height >= AppConfig.MIN_HEIGHT && height <= AppConfig.MAX_HEIGHT

    /**
     * Check weather [weight] is valid or not. Valid [weight] must be [AppConfig.MIN_WEIGHT] and
     * [AppConfig.MAX_WEIGHT].
     *
     * @return True if the [weight] is valid.
     */
    fun isValidWeight(weight: Float) = weight >= AppConfig.MIN_WEIGHT && weight <= AppConfig.MAX_WEIGHT

    fun isValidDeviceId(deviceId: String?) = !deviceId.isNullOrEmpty()

    fun isValidFcmId(fcmId: String?) = !fcmId.isNullOrEmpty()

    fun isValidIssueTitle(title: String?) = !title.isNullOrEmpty()

    fun isValidIssueDescription(description: String?) = !description.isNullOrEmpty()

    fun isValidDate(dayOfMonth: Int) = dayOfMonth in 1..31

    fun isValidMonth(monthOfYear: Int) = monthOfYear in 0..11

    fun isValidYear(year: Int) = year in 1900..2100
}