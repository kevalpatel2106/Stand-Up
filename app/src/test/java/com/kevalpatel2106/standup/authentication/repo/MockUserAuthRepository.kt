package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
import com.kevalpatel2106.standup.authentication.login.LoginRequest
import com.kevalpatel2106.standup.authentication.login.LoginResponseData
import com.kevalpatel2106.standup.authentication.signUp.SignUpRequest
import com.kevalpatel2106.standup.authentication.signUp.SignUpResponseData
import com.kevalpatel2106.testutils.MockWebserverUtils
import io.reactivex.Observable
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

class MockUserAuthRepository : UserAuthRepository, Closeable {
    private var mockWebServer: MockWebServer = MockWebserverUtils.startMockWebServer()

    override fun login(loginRequest: LoginRequest): Observable<Response<LoginResponseData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun signUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun socialSignUp(signUpRequest: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(UserAuthRepository::class.java)
                    .socialSignUp(signUpRequest)



    fun enqueueResponse(response: String) {
        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type","application/json")
                .setBody(response)
                .setResponseCode(HttpURLConnection.HTTP_OK))
    }

    fun enqueueResponse(response: File) {
        enqueueResponse(MockWebserverUtils.getStringFromFile(file = response))
    }

    override fun close() {
        mockWebServer.close()
    }
}
