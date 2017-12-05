package com.kevalpatel2106.standup.profile.repo

import com.kevalpatel2106.base.annotations.Repository
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresherImpl
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.utils.UserSessionManager
import com.kevalpatel2106.utils.toFloatSafe
import com.kevalpatel2106.vault.VaultBuilder
import com.kevalpatel2106.vault.VaultData
import com.kevalpatel2106.vault.cache.SharedPrefranceCache
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.net.ssl.HttpsURLConnection

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
class UserProfileRepoImpl(private val baseUrl: String) : UserProfileRepo {

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

            override fun read(): VaultData<GetProfileResponse> {
                if (!UserSessionManager.isUserLoggedIn) {
                    //User is not logged in.
                    //Cannot do any thing
                    val errorData = VaultData<GetProfileResponse>(true)
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
                    return VaultData(false, userProfile)
                } catch (e: Exception) {
                    val errorData = VaultData<GetProfileResponse>(true)
                    errorData.errorMessage = "Cannot get user profile."
                    errorData.errorCode = HttpsURLConnection.HTTP_UNAUTHORIZED
                    return errorData
                }
            }
        }

        return VaultBuilder<GetProfileResponse>()
                .addCache(cache)
                .addRefresher(RetrofitNetworkRefresherImpl(call))
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

        return VaultBuilder<SaveProfileResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
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