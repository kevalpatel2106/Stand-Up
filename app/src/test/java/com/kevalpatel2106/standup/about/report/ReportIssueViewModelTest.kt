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

package com.kevalpatel2106.standup.about.report

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.about.repo.AboutRepositoryImpl
import com.kevalpatel2106.testutils.MockServerManager
import com.kevalpatel2106.testutils.MockSharedPreference
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.kevalpatel2106.standup.misc.UserSessionManager
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import java.nio.file.Paths

/**
 * Created by Keval on 23/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReportIssueViewModelTest {
    private val path = Paths.get("").toAbsolutePath().toString().let {
        return@let if (it.endsWith("app")) it else it.plus("/app")
    }
    private val RESPONSE_DIR_PATH = String.format("%s/src/test/java/com/kevalpatel2106/standup/about/repo", path)

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

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
                ApiProvider().getRetrofitClient(mockServerManager.getBaseUrl()),    /* Mock web server */
                UserSessionManager(SharedPrefsProvider(MockSharedPreference()))     /* Mock shared prefrance*/
        ))
    }

    @Test
    fun checkReportIssueWithInvalidTitle() {
        model.reportIssue("", "This is test issue description.", "test_device_id")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorRes, R.string.error_invalid_issue_title)
    }

    @Test
    fun checkReportIssueWithInvalidDescription() {
        model.reportIssue("This is test issue title.", "", "test_device_id")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorRes, R.string.error_invalid_issue_description)
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
