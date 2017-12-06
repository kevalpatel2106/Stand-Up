package com.kevalpatel2106.standup.profile.repo

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import io.reactivex.subscribers.TestSubscriber
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
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class UserProfileRepoImplTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/profile/repo", File(File("").absolutePath))

    private val mockServerManager = MockServerManager()
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
        mockServerManager.startMockWebServer()
        mUserProfileRepoImpl = UserProfileRepoImpl(mockServerManager.getBaseUrl())
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun testGetUserProfileSuccess() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))


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
    @Throws(IOException::class)
    fun testGetUserProfileUserNotLogin() {
        //Mock the shared preference
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn(null)
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn(null)
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(anyString(), anyBoolean())).thenReturn(true)

        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))

        val testSubscriber = TestSubscriber<GetProfileResponse>()
        mUserProfileRepoImpl.getUserProfile(12345678).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { t -> t.email == "test@example.com" }
                .assertComplete()
    }

    @Test
    @Throws(IOException::class)
    fun testGetUserProfileUserNoCachedResult() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn(null)

        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/get_profile_success.json")))

        val testSubscriber = TestSubscriber<GetProfileResponse>()
        mUserProfileRepoImpl.getUserProfile(12345678).subscribe(testSubscriber)
        testSubscriber.awaitTerminalEvent()

        testSubscriber.assertNoErrors()
                .assertValueCount(1)
                .assertValueAt(0) { t -> t.email == "test@example.com" }
                .assertComplete()
    }

    @Test
    @Throws(IOException::class)
    fun testSaveUserProfile() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(anyString(), isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(anyString(), anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(anyString(), anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(mockServerManager
                .getStringFromFile(File(RESPONSE_DIR_PATH + "/save_profile_success.json")))

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