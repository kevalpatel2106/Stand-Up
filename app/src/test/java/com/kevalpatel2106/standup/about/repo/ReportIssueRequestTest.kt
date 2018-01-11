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

package com.kevalpatel2106.standup.about.repo

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 01-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class ReportIssueRequestTest {

    private val TEST_USER_ID = 12345678L
    private val TEST_TITLE = "test_title"
    private val TEST_DESCRIPTION = "test_description"
    private val TEST_DEVICE_ID = "test_device_id"

    @Before
    fun setUp() {
        val context = Mockito.mock(Context::class.java)
        val mockSharedPref = Mockito.mock(SharedPreferences::class.java)

        Mockito.`when`(mockSharedPref.getLong(anyString(), anyLong())).thenReturn(TEST_USER_ID)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()))
                .thenReturn(mockSharedPref)
    }

    @Test
    fun checkConstructor() {
        val reportIssue = ReportIssueRequest(TEST_USER_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DEVICE_ID)

        Assert.assertEquals(reportIssue.message, TEST_DESCRIPTION)
        Assert.assertEquals(reportIssue.title, TEST_TITLE)
        Assert.assertEquals(reportIssue.deviceId, TEST_DEVICE_ID)
    }

    @Test
    fun checkDeviceId() {
        val reportIssue = ReportIssueRequest(TEST_USER_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DEVICE_ID)
        Assert.assertEquals(reportIssue.deviceName, Utils.getDeviceName())
    }

    @Test
    fun checkUserId() {
        val reportIssue = ReportIssueRequest(TEST_USER_ID, TEST_TITLE, TEST_DESCRIPTION, TEST_DEVICE_ID)
        Assert.assertEquals(reportIssue.userId, TEST_USER_ID)
    }
}
