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

package com.standup.app.about

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.SharedPreferences
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.about.repo.AboutRepositoryImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File
import java.nio.file.Paths

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class AboutViewModelTest {
    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("feature-about")) it else it.plus("/feature-about")
    }
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/standup/app/about/repo", path)
    private val TEST_USER_ID: Long = 12L

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var viewModel: AboutViewModel
    private val mockServerManager = MockServerManager()


    @Before
    fun setUp() {
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(TEST_USER_ID)

        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        mockServerManager.startMockWebServer()
        val aboutRepository = AboutRepositoryImpl(
                NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl()),
                UserSessionManager(SharedPrefsProvider(sharedPrefs))
        )
        viewModel = AboutViewModel(aboutRepository)
    }


    @Test
    fun checkGetLatestVersionSuccessUpdateAvailable() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/get_latest_version_success_update_available.json"))

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)

        viewModel.checkForUpdate()

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.latestVersionName, "1.0")
        Assert.assertTrue(viewModel.versionUpdateResult.value!!.isUpdate)
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.latestVersionCode, 5000)
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.releaseNotes, "This is the release note for the version 3.")
    }

    @Test
    fun checkGetLatestVersionSuccessLatestVersionAvailable() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/get_latest_version_success_latest_available.json"))

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)

        viewModel.checkForUpdate()

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.latestVersionName, "1.0")
        Assert.assertFalse(viewModel.versionUpdateResult.value!!.isUpdate)
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.latestVersionCode, 1)
        Assert.assertNull(viewModel.versionUpdateResult.value!!.releaseNotes)
    }

    @Test
    fun checkGetLatestVersionError() {
        mockServerManager.enqueueResponse(File(RESPONSE_DIR_PATH + "/authentication_field_missing.json"))

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)

        viewModel.checkForUpdate()

        Assert.assertFalse(viewModel.isCheckingUpdate.value!!)
        Assert.assertEquals(viewModel.errorMessage.value!!.errorMessageRes, R.string.check_update_error_message)
    }
}
