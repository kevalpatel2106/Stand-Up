package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.standup.BuildConfig
import io.reactivex.Observable
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

    @POST("getProfile")
    fun getUserProfile(request: GetProfileRequest): Observable<GetProfileResponse>
}