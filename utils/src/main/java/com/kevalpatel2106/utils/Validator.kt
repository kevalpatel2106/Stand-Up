package com.kevalpatel2106.utils

import android.text.TextUtils
import java.util.regex.Pattern

/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object Validator {

    //Email pattern regex.
    private val EMAIL_PATTERN = ("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")

    //Password min/max length.
    private val MAX_PASSWORD = 16
    private val MIN_PASSWORD = 6

    //Display name min/max length.
    private val MIN_NAME = 6
    private val MAX_NAME = 30

    /**
     * Validate email address.
     * Regex pattern : [.EMAIL_PATTERN].
     *
     * @param email email to validate.
     * @return true if the email is valid.
     */
    fun isValidEmail(email: String?): Boolean {
        return (email != null
                && !email.trim { it <= ' ' }.isEmpty()
                && Pattern.compile(EMAIL_PATTERN).matcher(email).matches())
    }

    /**
     * Validate the password. The password should not contain all the spaces. Length should be
     * grater than [.MIN_PASSWORD] and less than or equal to [.MAX_PASSWORD].
     *
     * @param password password to check
     * @return true if the password is valid.
     */
    fun isValidPassword(password: String?): Boolean {
        return (password != null
                && !password.trim { it <= ' ' }.isEmpty()
                && password.length > MIN_PASSWORD
                && password.length <= MAX_PASSWORD)
    }

    /**
     * Validate the user display name. Length should be
     * grater than [.MIN_NAME] and less than or equal to [.MAX_NAME].
     *
     * @param name name of the user to validate.
     * @return true if the name is valid.
     */
    fun isValidName(name: String?): Boolean {
        return (name != null
                && !name.trim { it <= ' ' }.isEmpty()
                && name.length > MIN_NAME
                && name.length <= MAX_NAME)
    }
}