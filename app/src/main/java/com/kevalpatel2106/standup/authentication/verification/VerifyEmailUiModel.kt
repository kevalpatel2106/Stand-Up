package com.kevalpatel2106.standup.authentication.verification

import com.kevalpatel2106.base.annotations.Model

/**
 * Created by Kevalpatel2106 on 26-Nov-17.
 * This is view model to take appropriate actions on UI when the resend verification call response received.
 *
 * @property isSuccess Boolean to set true if the api response is success.
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Model
internal data class VerifyEmailUiModel(var isSuccess: Boolean)