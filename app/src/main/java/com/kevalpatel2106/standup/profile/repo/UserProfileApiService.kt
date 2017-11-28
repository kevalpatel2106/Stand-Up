package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.POST

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal interface UserProfileApiService {

    @POST
    fun getUserProfile(request: GetProfileRequest): Observable<Response<GetProfileResponse>>
}