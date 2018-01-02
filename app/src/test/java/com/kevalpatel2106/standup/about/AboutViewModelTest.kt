/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.about

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.about.repo.AboutRepositoryImpl
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
import java.nio.file.Paths

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class AboutViewModelTest{
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/about/repo", Paths.get("").toAbsolutePath().toString())

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rxRule: RxSchedulersOverrideRule = RxSchedulersOverrideRule()

    private lateinit var viewModel: AboutViewModel
    private val mockServerManager = MockServerManager()

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
    fun setUp(){
        mockServerManager.startMockWebServer()
        val aboutRepository = AboutRepositoryImpl(mockServerManager.getBaseUrl())
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
        Assert.assertEquals(viewModel.versionUpdateResult.value!!.releaseNotes,  "This is the release note for the version 3.")
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
        Assert.assertEquals(viewModel.errorMessage.value!!.errorRes, R.string.check_update_error_message)
    }
}