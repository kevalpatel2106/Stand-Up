package com.kevalpatel2106.standup.profile

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.UnitTestUtils
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.standup.profile.repo.UserProfileRepoImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import java.io.IOException
import java.nio.file.Paths

/**
 * Created by Kevalpatel2106 on 05-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class EditProfileModelTest {
    private val RESPONSE_DIR_PATH = String.format("%s/app/src/test/java/com/kevalpatel2106/standup/profile/repo", Paths.get(".").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var editProfileModel: EditProfileModel
    private val mockServerManager = MockServerManager()

    companion object {

        @JvmStatic
        @BeforeClass
        fun setGlobal() = UnitTestUtils.initApp()
    }

    private lateinit var sharedPrefs: SharedPreferences

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        SharedPrefsProvider.init(context)
        ApiProvider.init()

        //Set the repo
        mockServerManager.startMockWebServer()
        editProfileModel = EditProfileModel(UserProfileRepoImpl(mockServerManager.getBaseUrl()), false)
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(editProfileModel.blockUi.value!!)
    }


    @Test
    @Throws(IOException::class)
    fun checkLoadProfileSuccess() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(true)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/save_profile_success.json"))

        //Make the api call to the mock server
        editProfileModel.loadMyProfile()

        //There should be success.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNotNull(editProfileModel.mUserProfile.value)
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.email, "test@example.com")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.name, "Test User")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.userId, 5715999101812736)
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.height, "180.0")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.weight, "60.0")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.mUserProfile.value!!.isVerified)
        Assert.assertNull(editProfileModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkLoadProfileErrorWithCache() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(true)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.loadMyProfile()

        //There should be cache data in the profile model.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNotNull(editProfileModel.mUserProfile.value)
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.email, "149.3")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.name, "149.3")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.userId, 123456789)
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.height, "149.3")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.weight, "149.3")
        Assert.assertEquals(editProfileModel.mUserProfile.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.mUserProfile.value!!.isVerified)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkLoadProfileErrorWithoutCache() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.loadMyProfile()

        //There should be error.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNull(editProfileModel.mUserProfile.value)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkSaveProfileSuccess() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("test@example.com")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/save_profile_success.json"))

        //Make the api call to the mock server
        editProfileModel.saveMyProfile(name = "Test User", photo = null, isMale = true,
                weight = AppConfig.MIN_WEIGHT + 2, height = AppConfig.MAX_HEIGHT - 2)

        //There should be success.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNotNull(editProfileModel.mProfileUpdateStatus.value)
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.email, "test@example.com")
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.name, "Test User")
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.userId, 5715999101812736)
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.height, "180.0")
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.weight, "60.0")
        Assert.assertEquals(editProfileModel.mProfileUpdateStatus.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.mProfileUpdateStatus.value!!.isVerified)
        Assert.assertNull(editProfileModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkSaveProfileError() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("test@example.com")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.saveMyProfile(name = "Test User", photo = null, isMale = true,
                weight = AppConfig.MIN_WEIGHT + 2, height = AppConfig.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNull(editProfileModel.mProfileUpdateStatus.value)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.getMessage(null), "Required field missing.")
    }

    @Test
    @Throws(IOException::class)
    fun checkSaveProfileInvalidNameError() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("test@example.com")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.saveMyProfile(name = "Test", photo = null, isMale = true,
                weight = AppConfig.MIN_WEIGHT + 2, height = AppConfig.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorRes, R.string.error_login_invalid_name)
    }

    @Test
    @Throws(IOException::class)
    fun checkSaveProfileInvalidWeightError() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("test@example.com")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.saveMyProfile(name = "Test User", photo = null, isMale = true,
                weight = AppConfig.MIN_WEIGHT - 2, height = AppConfig.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorRes, R.string.error_invalid_weight)
    }

    @Test
    @Throws(IOException::class)
    fun checkSaveProfileInvalidHeightError() {
        //Mock the shared prefrance
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("test@example.com")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/profile_field_missing.json"))

        //Make the api call to the mock server
        editProfileModel.saveMyProfile(name = "Test User", photo = null, isMale = true,
                weight = AppConfig.MIN_WEIGHT + 2, height = AppConfig.MAX_HEIGHT + 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorRes, R.string.error_invalid_height)
    }
}