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
    override fun logout(logoutRequest: LogoutRequest): Flowable<LogoutResponse> {
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

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Flowable<ForgotPasswordResponse> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .forgotPassword(forgotPasswordRequest)

        return VaultBuilder<ForgotPasswordResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Flowable<ResendVerificationResponse> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .resendVerifyEmail(request)

        return VaultBuilder<ResendVerificationResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun registerDevice(request: DeviceRegisterRequest): Flowable<DeviceRegisterResponse> {
        throw NotImplementedError("No required.")
    }

    override fun login(loginRequest: LoginRequest): Flowable<LoginResponse> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .login(loginRequest)

        return VaultBuilder<LoginResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun signUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .signUp(signUpRequest)

        return VaultBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Flowable<SignUpResponse> {
        val call = ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserApiService::class.java)
                .socialSignUp(signUpRequest)

        return VaultBuilder<SignUpResponse>()
                .addRefresher(RetrofitNetworkRefresherImpl(call))
                .build()
                .fetch()
                .map { t -> t.data }    //We don't use vault data information on the UI level. Map it to simple.
    }
}
