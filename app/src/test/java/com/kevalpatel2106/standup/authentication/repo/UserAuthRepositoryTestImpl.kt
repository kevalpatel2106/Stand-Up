package com.kevalpatel2106.standup.authentication.repo

import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.Response
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

class UserAuthRepositoryTestImpl : UserAuthRepository, Closeable {
    private var mockWebServer: MockWebServer = MockWebserverUtils.startMockWebServer()

    fun enqueueResponse(response: String) {
        mockWebServer.enqueue(MockResponse().setBody(response).setResponseCode(HttpURLConnection.HTTP_OK))
    }

    fun enqueueResponse(response: File) {
        enqueueResponse(MockWebserverUtils.getStringFromFile(file = response))
    }

    override fun close() {
        mockWebServer.close()
    }

    /**
     * This method provides [Observable] to login/sign up user using the social accounts.
     */
    override fun authSocialUser(requestData: SignUpRequest): Observable<Response<SignUpResponseData>> =
            ApiProvider.getRetrofitClient(MockWebserverUtils.getBaseUrl(mockWebServer))
                    .create(AuthApiService::class.java)
                    .socialSignUp(requestData)

}
