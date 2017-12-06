package com.kevalpatel2106.testutils

import android.content.Context
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import java.io.*
import java.net.HttpURLConnection

/**
 * Created by Keval on 05/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class MockServerManager : Closeable {

    lateinit var mockWebServer: MockWebServer

    /**
     * Start mock web server for the wikipedia api.
     *
     * @return [MockWebServer]
     */
    fun startMockWebServer(): MockWebServer {
        try {
            mockWebServer = MockWebServer()
            mockWebServer.start()
            return mockWebServer
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Failed to start mock server.")
        }

    }

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
        enqueueResponse(response = getStringFromFile(file = response), type = type)
    }


    fun getBaseUrl() = mockWebServer.url("/").toString()

    private fun getStringFromInputStream(`is`: InputStream): String {
        var br: BufferedReader? = null
        val sb = StringBuilder()

        try {
            br = BufferedReader(InputStreamReader(`is`))
            var hasNextLine = true
            while (hasNextLine) {
                val line = br.readLine()
                hasNextLine = line != null
                line?.let { sb.append(it) }
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return sb.toString()
    }

    fun getStringFromFile(context: Context, filename: Int): String =
            getStringFromInputStream(context.resources.openRawResource(filename))


    fun getStringFromFile(file: File): String = getStringFromInputStream(FileInputStream(file))
    override fun close() {
        mockWebServer.shutdown()
    }

}