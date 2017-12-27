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

package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.base.repository.RepoBuilder
import com.kevalpatel2106.base.repository.RepoData
import com.kevalpatel2106.base.repository.cache.SharedPrefranceCache
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresher
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.toFloatSafe
import io.reactivex.Flowable
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class UserProfileRepoImpl(private val baseUrl: String) : UserProfileRepo {

    constructor() : this(ProfileApiService.baseUrl())

    override fun getUserProfile(userId: Long): Flowable<GetProfileResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(ProfileApiService::class.java)
                .getUserProfile(GetProfileRequest(userId))

        val cache = object : SharedPrefranceCache<GetProfileResponse>(SharedPrefsProvider.sSharedPreference) {
            override fun write(t: GetProfileResponse?) {
                t?.let {
                    //Save to the cache.
                    UserSessionManager.updateSession(displayName = it.name,
                            weight = it.weight.toFloatSafe(),
                            height = it.height.toFloatSafe(),
                            photoUrl = null,
                            isMale = it.gender.toLowerCase().trim() == AppConfig.GENDER_MALE)
                }
            }

            override fun read(): RepoData<GetProfileResponse> {
                if (!UserSessionManager.isUserLoggedIn) {
                    //User is not logged in.
                    //Cannot do any thing
                    val errorData = RepoData<GetProfileResponse>(true)
                    errorData.errorMessage = "User is not logged in."
                    errorData.errorCode = HttpsURLConnection.HTTP_UNAUTHORIZED
                    return errorData
                }

                try {
                    val userProfile = GetProfileResponse(
                            userId = UserSessionManager.userId,
                            isVerified = UserSessionManager.isUserVerified,
                            email = UserSessionManager.email ?: "",
                            weight = UserSessionManager.weight.toString(),
                            height = UserSessionManager.height.toString(),
                            name = UserSessionManager.displayName ?: "",
                            photo = UserSessionManager.photo,
                            gender = if (UserSessionManager.isMale) AppConfig.GENDER_MALE else AppConfig.GENDER_FEMALE
                    )
                    return RepoData(false, userProfile)
                } catch (e: Exception) {
                    val errorData = RepoData<GetProfileResponse>(true)
                    errorData.errorMessage = "Cannot get user profile."
                    errorData.errorCode = HttpsURLConnection.HTTP_UNAUTHORIZED
                    return errorData
                }
            }
        }

        return RepoBuilder<GetProfileResponse>()
                .addCache(cache)
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { it.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun saveUserProfile(name: String,
                                 photo: String?,
                                 height: Float,
                                 weight: Float,
                                 isMale: Boolean): Flowable<SaveProfileResponse> {
        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(ProfileApiService::class.java)
                .saveUserProfile(SaveProfileRequest(userId = UserSessionManager.userId,
                        name = name,
                        photo = photo,
                        email = UserSessionManager.email!!,
                        height = height.toString(),
                        weight = weight.toString(),
                        gender = if (isMale) AppConfig.GENDER_MALE else AppConfig.GENDER_FEMALE))

        return RepoBuilder<SaveProfileResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t ->
                    t.data?.let {
                        UserSessionManager.updateSession(displayName = it.name,
                                weight = it.weight.toFloatSafe(),
                                height = it.height.toFloatSafe(),
                                photoUrl = null,
                                isMale = it.gender.toLowerCase().trim() == AppConfig.GENDER_MALE)
                    }
                    t.data
                }
    }
}