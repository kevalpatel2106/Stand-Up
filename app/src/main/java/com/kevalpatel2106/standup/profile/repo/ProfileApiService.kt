package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.standup.BuildConfig
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
    companion object {
        fun baseUrl() = BuildConfig.BASE_URL
    }

    @Headers("Add-Auth: true")
    @POST("getProfile")
    fun getUserProfile(@Body request: GetProfileRequest): Call<GetProfileResponse>

    @Headers("Add-Auth: true")
    @POST("saveProfile")
    fun saveUserProfile(@Body request: SaveProfileRequest): Call<SaveProfileResponse>
}