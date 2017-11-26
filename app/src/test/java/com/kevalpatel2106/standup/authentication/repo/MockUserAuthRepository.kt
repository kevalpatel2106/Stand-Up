package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.testutils.MockRepository
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.Observable
import java.io.Closeable


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class MockUserAuthRepository : MockRepository(), UserAuthRepository, Closeable {
    override fun forgotPassword(forgotPasswordRequest: ForgotPasswordRequest): Observable<Response<ForgotPasswordResponseData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .forgotPassword(forgotPasswordRequest)
    }

    override fun resendVerifyEmail(request: ResendVerificationRequest): Observable<Response<ResendVerificationResponseData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .resendVerifyEmail(request)
    }

    override fun registerDevice(request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>> {
        throw NotImplementedError("No required.")
    }

    override fun login(loginRequest: LoginRequest): Observable<Response<LoginResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .login(loginRequest)

    override fun signUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .signUp(signUpRequest)

    override fun socialSignUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .socialSignUp(signUpRequest)
}
