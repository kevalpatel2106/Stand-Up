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

package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.repository.RepoBuilder
import com.kevalpatel2106.common.repository.RepoData
import com.kevalpatel2106.common.repository.cache.Cache
import com.kevalpatel2106.common.repository.refresher.RetrofitNetworkRefresher
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.utils.annotations.Repository
import com.kevalpatel2106.utils.toFloatSafe
import io.reactivex.Flowable
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Named
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
internal class UserProfileRepoImpl @Inject constructor(@Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit,
                                                       private val userSessionManager: UserSessionManager)
    : UserProfileRepo {


    override fun getUserProfile(userId: Long): Flowable<GetProfileResponse> {
        val call = retrofit.create(ProfileApiService::class.java)
                .getUserProfile(GetProfileRequest(userId))

        val cache = object : Cache<GetProfileResponse> {
            override fun write(t: GetProfileResponse?) {
                t?.let {
                    //Save to the cache.
                    userSessionManager.updateSession(displayName = it.name,
                            weight = it.weight.toFloatSafe(),
                            height = it.height.toFloatSafe(),
                            photoUrl = null,
                            isMale = it.gender.toLowerCase().trim() == AppConfig.GENDER_MALE)
                }
            }

            override fun read(): RepoData<GetProfileResponse> {
                if (!userSessionManager.isUserLoggedIn) {
                    //User is not logged in.
                    //Cannot do any thing
                    val errorData = RepoData<GetProfileResponse>(true)
                    errorData.errorMessage = "User is not logged in."
                    errorData.errorCode = HttpsURLConnection.HTTP_UNAUTHORIZED
                    return errorData
                }

                try {
                    val userProfile = GetProfileResponse(
                            userId = userSessionManager.userId,
                            isVerified = userSessionManager.isUserVerified,
                            email = userSessionManager.email ?: "",
                            weight = userSessionManager.weight.toString(),
                            height = userSessionManager.height.toString(),
                            name = userSessionManager.displayName ?: "",
                            photo = userSessionManager.photo,
                            gender = if (userSessionManager.isMale) AppConfig.GENDER_MALE else AppConfig.GENDER_FEMALE
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
        val call = retrofit
                .create(ProfileApiService::class.java)
                .saveUserProfile(SaveProfileRequest(userId = userSessionManager.userId,
                        name = name,
                        photo = photo,
                        email = userSessionManager.email!!,
                        height = height.toString(),
                        weight = weight.toString(),
                        gender = if (isMale) AppConfig.GENDER_MALE else AppConfig.GENDER_FEMALE))

        return RepoBuilder<SaveProfileResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t ->
                    t.data?.let {
                        userSessionManager.updateSession(displayName = it.name,
                                weight = it.weight.toFloatSafe(),
                                height = it.height.toFloatSafe(),
                                photoUrl = null,
                                isMale = it.gender.toLowerCase().trim() == AppConfig.GENDER_MALE)
                    }
                    t.data
                }
    }
}
