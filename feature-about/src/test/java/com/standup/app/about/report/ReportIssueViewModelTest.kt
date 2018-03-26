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

package com.standup.app.about.report

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.SharedPreferences
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.network.NetworkApi
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.standup.app.about.BuildConfig
import com.standup.app.about.R
import com.standup.app.about.repo.AboutRepositoryImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.io.File

/**
 * Created by Keval on 23/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReportIssueViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private val mockServerManager = MockServerManager()
    private lateinit var model: ReportIssueViewModel

    @Before
    fun setUp() {
        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        Mockito.`when`(sharedPrefs.getLong(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(12L)

        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        mockServerManager.startMockWebServer()

        model = ReportIssueViewModel(AboutRepositoryImpl(
                NetworkApi().getRetrofitClient(mockServerManager.getBaseUrl()),    /* Mock web server */
                UserSessionManager(SharedPrefsProvider(MockSharedPreference()))     /* Mock shared prefrance*/
        ))
    }

    @Test
    fun checkReportIssueWithInvalidTitle() {
        model.reportIssue("", "This is test issue description.", "test_device_id")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorMessageRes, R.string.error_invalid_issue_title)
    }

    @Test
    fun checkReportIssueWithInvalidDescription() {
        model.reportIssue("This is test issue title.", "", "test_device_id")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorMessageRes, R.string.error_invalid_issue_description)
    }

    @Test
    fun testCheckForUpdatesWithUpdateAvailable() {
        mockServerManager.enqueueResponse(File("${mockServerManager.getResponsesPath()}/get_latest_version_success_update_available.json"))

        model.checkForUpdate()

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertEquals(model.versionUpdateResult.value!!.latestVersionName, "1.0")
        Assert.assertEquals(model.versionUpdateResult.value!!.latestVersionCode, 10000000)
        Assert.assertEquals(model.versionUpdateResult.value!!.latestVersionCode > BuildConfig.VERSION_CODE,
                model.versionUpdateResult.value!!.isUpdate)
        Assert.assertEquals(model.versionUpdateResult.value!!.releaseNotes, "This is the release note for the version 3.")
    }

    @Test
    fun testCheckForUpdatesWithUpdateNotAvailable() {
        mockServerManager.enqueueResponse(File("${mockServerManager.getResponsesPath()}/get_latest_version_success_latest_available.json"))

        model.checkForUpdate()

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.versionUpdateResult.value)
    }

    @Test
    fun testCheckForUpdatesFail() {
        mockServerManager.enqueueResponse(File("${mockServerManager.getResponsesPath()}/authentication_field_missing.json"))

        model.checkForUpdate()

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorMessageRes, R.string.check_update_error_message)
        Assert.assertNull(model.versionUpdateResult.value)
    }


    @Test
    fun checkReportIssueWithInvalidDeviceId() {
        try {
            model.reportIssue(
                    "This is test issue title.",
                    "This is test issue description.",
                    ""
            )
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
            //No OP
        }
    }

}
