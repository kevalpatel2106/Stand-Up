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