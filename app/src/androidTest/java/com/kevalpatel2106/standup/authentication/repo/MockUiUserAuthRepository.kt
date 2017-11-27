package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.testutils.MockRepository
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.Closeable


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class MockUiUserAuthRepository : MockRepository(), UserAuthRepository, Closeable {

    fun getBase() = MockWebserverUtils.getBaseUrl(mockWebServer)

    override fun logout(logoutRequest: LogoutRequest): Observable<Response<LoginResponseData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .logout(logoutRequest)
    }

    override fun verifyEmailLink(url: String): Observable<String> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .verifyEmailLink(url.replace(com.kevalpatel2106.standup.BuildConfig.BASE_URL,
                        MockWebserverUtils.getBaseUrl(mockWebServer)))
    }

    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Observable<Response<ForgotPasswordResponseData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .forgotPassword(forgotPasswordRequest)
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Observable<Response<ResendVerificationResponseData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .resendVerifyEmail(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun registerDevice(request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .registerDevice(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
    }

    override fun login(loginRequest: LoginRequest): Observable<Response<LoginResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .login(loginRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

    override fun signUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .signUp(signUpRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())

    override fun socialSignUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> =
            throw NotImplementedError("No required.")
}
