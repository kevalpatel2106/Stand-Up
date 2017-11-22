package com.kevalpatel2106.testutils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.Closeable
import java.io.File
import java.net.HttpURLConnection

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class MockRepository: Closeable{
    /**
     * Mock web server.
     */
    protected var mockWebServer: MockWebServer = MockWebserverUtils.startMockWebServer()

    /**
     * Enqueue the next response in [mockWebServer].
     */
    fun enqueueResponse(response: String) {
        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type","application/json")
                .setBody(response)
                .setResponseCode(HttpURLConnection.HTTP_OK))
    }

    /**
     * Enqueue the next response in [mockWebServer].
     */
    fun enqueueResponse(response: File) {
        enqueueResponse(MockWebserverUtils.getStringFromFile(file = response))
    }

    override fun close() {
        mockWebServer.shutdown()
    }
}