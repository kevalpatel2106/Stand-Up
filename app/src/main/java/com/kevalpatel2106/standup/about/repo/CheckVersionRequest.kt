/*
 *  Copyright 2018 Keval Patel.
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

package com.kevalpatel2106.standup.about.repo

import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.utils.annotations.Model

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 * Request POKO for the [AboutApiService.getLatestVersion] endpoint.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see AboutApiService.getLatestVersion
 */
@Model
class CheckVersionRequest {

    companion object {

        /**
         * Name of android platform as defined on the backend.
         */
        const val PLATFORM_NAME_ANDROID = "android"
    }

    /**
     * Name of the application platform. For this code base it will be [PLATFORM_NAME_ANDROID].
     */
    @SerializedName("platform")
    val platform = PLATFORM_NAME_ANDROID

    override fun equals(other: Any?): Boolean {
        other?.let {
            if (other is CheckVersionRequest) {
                return other.platform == platform
            } else if (other is String) {
                return other == platform
            }
        }
        return false
    }

    override fun hashCode(): Int = platform.hashCode()
}
