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

package com.standup.app.profile.repo

import com.kevalpatel2106.utils.annotations.Repository
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by Kevalpatel2106 on 29-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
interface ProfileApiService {

    @Headers("Add-Auth: true")
    @POST("getProfile")
    fun getUserProfile(@Body request: GetProfileRequest): Call<GetProfileResponse>

    @Headers("Add-Auth: true")
    @POST("updateProfile")
    fun saveUserProfile(@Body request: SaveProfileRequest): Call<SaveProfileResponse>
}
