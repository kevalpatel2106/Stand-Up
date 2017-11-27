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
abstract class MockRepository : Closeable {
    /**
     * Mock web server.
     */
    protected var mockWebServer: MockWebServer = MockWebserverUtils.startMockWebServer()

    /**
     * Enqueue the next response in [mockWebServer].
     */
    @JvmOverloads
    fun enqueueResponse(response: String, type: String = "application/json") {
        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type", type)
                .setBody(response)
                .setResponseCode(HttpURLConnection.HTTP_OK))
    }

    /**
     * Enqueue the next response in [mockWebServer].
     */
    @JvmOverloads
    fun enqueueResponse(response: File, type: String = "application/json") {
        enqueueResponse(response = MockWebserverUtils.getStringFromFile(file = response), type = type)
    }

    override fun close() {
        mockWebServer.shutdown()
    }
}