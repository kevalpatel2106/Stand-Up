package com.kevalpatel2106.standup.authentication.forgotPwd

import com.kevalpatel2106.base.annotations.Model

/**
 * Created by Kevalpatel2106 on 26-Nov-17.
 * This is view model to take appropriate actions on UI when the resend verification call response received.
 *
 * @property isSuccess Boolean to set true if the api response is success.
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
internal data class ForgotPasswordUiModel(var isSuccess: Boolean) {

    /**
     * Error message received from the server if [isSuccess] is false else it will be null.
     */
    var errorMsg: String? = null
        get() = if (isSuccess) null else field
}