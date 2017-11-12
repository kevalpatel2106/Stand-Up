package com.kevalpatel2106.testutils

import android.content.Context
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.fail
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


/**
 * Created by Keval on 12/11/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

object MockWebserverUtils {

    /**
     * Start mock web server for the wikipedia api.
     *
     * @return [MockWebServer]
     */
    fun startMockWebServer(): MockWebServer {
        try {
            val mockWebServer = MockWebServer()
            mockWebServer.start()
            return mockWebServer
        } catch (e: IOException) {
            e.printStackTrace()
            fail("Failed to startTimeMills mock server.")
            throw RuntimeException("Failed to startTimeMills mock server.")
        }

    }

    fun getBaseUrl(mockWebServer: MockWebServer): String {
        return mockWebServer.url("/").toString()
    }

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

    fun getStringFromFile(context: Context, filename: Int): String {
        return getStringFromInputStream(context.resources.openRawResource(filename))
    }
}
