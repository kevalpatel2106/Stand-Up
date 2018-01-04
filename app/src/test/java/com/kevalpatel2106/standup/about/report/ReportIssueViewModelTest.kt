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
import com.kevalpatel2106.standup.R
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

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

    private lateinit var model: ReportIssueViewModel

    @Before
    fun setUp() {
        model = ReportIssueViewModel()
    }

    @Test
    fun checkReportIssueWithInvalidTitle() {
        model.reportIssue("", "This is test issue description.", "test_device_id")
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertEquals(model.errorMessage.value!!.errorRes, R.string.error_invalid_issue_title)
    }

    @Test
    fun checkReportIssueWithInvalidDescription() {
        model.reportIssue("This is test issue title.", "","test_device_id")
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
        }catch (e :IllegalArgumentException){
            //Test passed
            //No OP
        }
    }

}