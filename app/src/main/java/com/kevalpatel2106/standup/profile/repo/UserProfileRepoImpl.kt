package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresherImpl
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.vault.VaultBuilder
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
class UserProfileRepoImpl : UserProfileRepo {

    override fun getUserProfile(userId: Long): Flowable<GetProfileResponse> {
        val call = ApiProvider.getRetrofitClient(ProfileApiService.baseUrl())
                .create(ProfileApiService::class.java)
                .getUserProfile(GetProfileRequest(userId))

        return VaultBuilder<GetProfileResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun saveUserProfile(name: String,
                                 photo: String?,
                                 height: Float,
                                 weight: Float,
                                 isMale: Boolean): Flowable<SaveProfileResponse> {
        val call = ApiProvider.getRetrofitClient(ProfileApiService.baseUrl())
                .create(ProfileApiService::class.java)
                .saveUserProfile(SaveProfileRequest(userId = UserSessionManager.userId,
                        name = name,
                        photo = photo,
                        email = UserSessionManager.email!!,
                        height = height.toString(),
                        weight = weight.toString(),
                        gender = if (isMale) "male" else "female"))

        return VaultBuilder<SaveProfileResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { t ->
                    t.data?.let {
                        UserSessionManager.updateSession(displayName = it.name,
                                weight = it.weight.toFloat(),
                                height = it.height.toFloat(),
                                photoUrl = null,
                                isMale = it.gender.toLowerCase().trim() == "male")
                    }
                    t.data
                }
    }
}