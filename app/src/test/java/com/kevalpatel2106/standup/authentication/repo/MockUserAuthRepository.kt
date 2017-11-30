package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresherImpl
import com.kevalpatel2106.testutils.MockRepository
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.vault.VaultBuilder
import io.reactivex.Flowable
import java.io.Closeable


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class MockUserAuthRepository : MockRepository(), UserAuthRepository, Closeable {
    override fun logout(logoutRequest: LogoutRequest): Flowable<LogoutResponseData> {
        throw NotImplementedError("No required.")
    }

    override fun verifyEmailLink(url: String): Flowable<String> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .verifyEmailLink(url.replace(com.kevalpatel2106.standup.BuildConfig.BASE_URL,
                        MockWebserverUtils.getBaseUrl(mockWebServer)))

        return VaultBuilder<String>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponseData> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .forgotPassword(forgotPasswordRequest)

        return VaultBuilder<ForgotPasswordResponseData>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Flowable<ResendVerificationResponseData> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .resendVerifyEmail(request)

        return VaultBuilder<ResendVerificationResponseData>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun registerDevice(request: DeviceRegisterRequest): Flowable<DeviceRegisterData> {
        throw NotImplementedError("No required.")
    }

    override fun login(loginRequest: LoginRequest): Flowable<LoginResponseData> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .login(loginRequest)

        return VaultBuilder<LoginResponseData>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun signUp(signUpRequest: SignUpRequest): Flowable<SignUpResponseData> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .signUp(signUpRequest)

        return VaultBuilder<SignUpResponseData>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Flowable<SignUpResponseData> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .socialSignUp(signUpRequest)

        return VaultBuilder<SignUpResponseData>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }
}
