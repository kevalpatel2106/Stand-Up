package com.kevalpatel2106.standup.authentication.intro

import com.kevalpatel2106.base.annotations.Model

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 * This is view model to take appropriate actions on UI when the login/sign up call response received.
 *
 * @property isSuccess Boolean to set true if the api response is success.
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
internal data class IntroUiModel(var isSuccess: Boolean) {

    /**
     * Boolean to indicate weather the user is new user/signed up.
     */
    var isNewUser: Boolean = false
        get() = if (!isSuccess) false else field

}