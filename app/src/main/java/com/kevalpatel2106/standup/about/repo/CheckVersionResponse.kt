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
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.utils.annotations.Model

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 * Response POKO for the [AboutApiService.getLatestVersion] endpoint.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see AboutApiService.getLatestVersion
 */
@Model
data class CheckVersionResponse(

        /**
         * Name of the latest version of available application. (e.g. 1.1.0)
         */
        @SerializedName("latestVersionName")
        val latestVersionName: String,

        /**
         * Version code of the latest available application. (e.g. 45) It must be positive, non-zero
         * integer value. Check [com.kevalpatel2106.standup.misc.Validator.isValidVersionCode] for
         * validation.
         *
         * @see com.kevalpatel2106.standup.misc.Validator.isValidVersionCode
         */
        @SerializedName("latestVersionCode")
        val latestVersionCode: Int,

        /**
         * Release note /Changelog for the latest version. (e.g. Bug fixes.) This may be null.
         */
        @SerializedName("releaseNotes")
        val releaseNotes: String?
) {

    /**
     * Boolean to set tru if there is new update available or false. It checks [latestVersionCode]
     * with the [BuildConfig.VERSION_CODE] and if the [latestVersionCode] is higher than
     * [BuildConfig.VERSION_CODE] indicates new version for the application is available.
     */
    val isUpdate: Boolean
        get() = latestVersionCode > BuildConfig.VERSION_CODE

    init {

        //Validate the version code
        if (!Validator.isValidVersionCode(latestVersionCode)) {
            throw IllegalArgumentException("Version name must be positive non-zero number. Current: "
                    .plus(latestVersionCode))
        }
    }
}
