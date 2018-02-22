/*
 *  Copyright 2018 Keval Patel.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.standup.app.profile

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.common.AppConfig
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.profile.repo.UserProfileRepoImpl
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
    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("feature-profile")) it else it.plus("/feature-profile")
    }
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/standup/app/profile/repo", path)

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var editProfileModel: EditProfileModel
    private val mockServerManager = MockServerManager()

    private lateinit var sharedPrefs: SharedPreferences

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        //Set the repo
        mockServerManager.startMockWebServer()
        editProfileModel = EditProfileModel(
                UserProfileRepoImpl(
                        NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl()),
                        UserSessionManager(SharedPrefsProvider(sharedPrefs))
                ),
                UserSessionManager(SharedPrefsProvider(sharedPrefs))
        )
    }

    @After
    fun tearUp() {
        mockServerManager.close()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitialization() {
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertFalse(editProfileModel.isSavingProfile.value!!)
        Assert.assertFalse(editProfileModel.blockUi.value!!)
    }


    @Test
    @Throws(IOException::class)
    fun checkLoadProfileSuccess() {
        //Mock the shared preference
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.isNull())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("149.3")
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(123456789)
        Mockito.`when`(sharedPrefs.getBoolean(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean())).thenReturn(true)

        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/save_profile_success.json"))

        //Make the api call to the mock server
        editProfileModel.loadMyProfile()

        //There should be success.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNotNull(editProfileModel.userProfile.value)
        Assert.assertEquals(editProfileModel.userProfile.value!!.email, "test@example.com")
        Assert.assertEquals(editProfileModel.userProfile.value!!.name, "Test User")
        Assert.assertEquals(editProfileModel.userProfile.value!!.userId, 5715999101812736)
        Assert.assertEquals(editProfileModel.userProfile.value!!.height, "180.0")
        Assert.assertEquals(editProfileModel.userProfile.value!!.weight, "60.0")
        Assert.assertEquals(editProfileModel.userProfile.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.userProfile.value!!.isVerified)
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
        Assert.assertNotNull(editProfileModel.userProfile.value)
        Assert.assertEquals(editProfileModel.userProfile.value!!.email, "149.3")
        Assert.assertEquals(editProfileModel.userProfile.value!!.name, "149.3")
        Assert.assertEquals(editProfileModel.userProfile.value!!.userId, 123456789)
        Assert.assertEquals(editProfileModel.userProfile.value!!.height, "149.3")
        Assert.assertEquals(editProfileModel.userProfile.value!!.weight, "149.3")
        Assert.assertEquals(editProfileModel.userProfile.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.userProfile.value!!.isVerified)
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
        Assert.assertNull(editProfileModel.userProfile.value)
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
                weight = Validator.MIN_WEIGHT + 2, height = Validator.MAX_HEIGHT - 2)

        //There should be success.
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNotNull(editProfileModel.profileUpdateStatus.value)
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.email, "test@example.com")
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.name, "Test User")
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.userId, 5715999101812736)
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.height, "180.0")
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.weight, "60.0")
        Assert.assertEquals(editProfileModel.profileUpdateStatus.value!!.gender, AppConfig.GENDER_MALE)
        Assert.assertTrue(editProfileModel.profileUpdateStatus.value!!.isVerified)
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
                weight = Validator.MIN_WEIGHT + 2, height = Validator.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertNull(editProfileModel.profileUpdateStatus.value)
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
                weight = Validator.MIN_WEIGHT + 2, height = Validator.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorMessageRes, R.string.error_login_invalid_name)
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
                weight = Validator.MIN_WEIGHT - 2, height = Validator.MAX_HEIGHT - 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorMessageRes, R.string.error_invalid_weight)
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
                weight = Validator.MIN_WEIGHT + 2, height = Validator.MAX_HEIGHT + 2)

        //There should be error
        Assert.assertFalse(editProfileModel.blockUi.value!!)
        Assert.assertEquals(editProfileModel.errorMessage.value!!.errorMessageRes, R.string.error_invalid_height)
    }
}
