package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterData
import com.kevalpatel2106.standup.authentication.deviceReg.DeviceRegisterRequest
import com.kevalpatel2106.testutils.MockRepository
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.Closeable
import java.io.File
import java.net.HttpURLConnection


/**
 * Created by Keval on 20/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

class MockUserAuthRepository : MockRepository(), UserAuthRepository, Closeable {


    override fun registerDevice(request: DeviceRegisterRequest): Observable<Response<DeviceRegisterData>> {
        return ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                .create(UserAuthRepository::class.java)
                .registerDevice(request)
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
