package com.kevalpatel2106.standup.profile.repo

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.testutils.MockWebserverUtils
import com.kevalpatel2106.utils.SharedPrefsProvider
import io.reactivex.subscribers.TestSubscriber
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import java.io.File
import java.net.HttpURLConnection

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class UserProfileRepoImplTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/profile/repo", File(File("").absolutePath))

    private lateinit var mockWebServer: MockWebServer
    private lateinit var mUserProfileRepoImpl: UserProfileRepoImpl

    companion object {

        private lateinit var sharedPrefs: SharedPreferences

        @JvmStatic
        @BeforeClass
        fun setUpClass() {
            val context = Mockito.mock(Context::class.java)
            sharedPrefs = Mockito.mock(SharedPreferences::class.java)
            val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
            Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
            Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

            SharedPrefsProvider.init(context)
            ApiProvider.init()
        }
    }

    @Before
    fun setUp() {
        mockWebServer = MockWebserverUtils.startMockWebServer()
        mUserProfileRepoImpl = UserProfileRepoImpl(MockWebserverUtils.getBaseUrl(mockWebServer))
    }

    @After
    fun tearUp() {
        mockWebServer.shutdown()
    }

    @Test
    fun testGetUserProfileSuccess() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))
                .setResponseCode(HttpURLConnection.HTTP_OK))

        val testSubscriber = TestSubscriber<GetProfileResponse>()
        mUserProfileRepoImpl.getUserProfile(12345678).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(2)
                .assertValueAt(0) { t -> t.email == "149.3" }
                .assertValueAt(1) { t -> t.email == "test@example.com" }
                .assertComplete()
    }

    @Test
    fun testGetUserProfileUserNotLogin() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn(null)
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn(null)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))
                .setResponseCode(HttpURLConnection.HTTP_OK))

        val testSubscriber = TestSubscriber<GetProfileResponse>()
        mUserProfileRepoImpl.getUserProfile(12345678).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { t -> t.email == "test@example.com" }
                .assertComplete()
    }

    @Test
    fun testGetUserProfileUserNoCachedResult() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn(null)

        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))
                .setResponseCode(HttpURLConnection.HTTP_OK))

        val testSubscriber = TestSubscriber<GetProfileResponse>()
        mUserProfileRepoImpl.getUserProfile(12345678).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { t -> t.email == "test@example.com" }
                .assertComplete()
    }

    @Test
    fun testSaveUserProfile() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)

        mockWebServer.enqueue(MockResponse()
                .setHeader("Content-type", "application/json")
                .setBody(MockWebserverUtils.getStringFromFile(File(RESPONSE_DIR_PATH + "/save_profile_success.json")))
                .setResponseCode(HttpURLConnection.HTTP_OK))

        val testSubscriber = TestSubscriber<SaveProfileResponse>()
        mUserProfileRepoImpl.saveUserProfile(name = "Test User",
                isMale = true,
                photo = "http://google.com",
                height = 123.45F,
                weight = 67.5F).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { t -> t.email == "test@example.com" }
                .assertComplete()
    }
}