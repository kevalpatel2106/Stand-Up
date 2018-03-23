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

package com.standup.app.billing

import android.content.Context
import android.support.v4.app.FragmentManager
import com.android.billingclient.api.BillingClient
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class InAppFailDialogUnitTest {

    private lateinit var context: Context
    private lateinit var fragmentManager: FragmentManager

    @Before
    fun setUp() {
        context = Mockito.mock(Context::class.java)
        fragmentManager = Mockito.mock(FragmentManager::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun checkLaunch_UserCancel() {
        Assert.assertFalse(InAppFailDialog.launch(context, fragmentManager, BillingClient.BillingResponse.USER_CANCELED))
    }

    @Test
    @Throws(Exception::class)
    fun checkLaunch_BillingCodeOk() {
        try {
            InAppFailDialog.launch(context, fragmentManager, BillingClient.BillingResponse.OK)
            Assert.fail()
        } catch (e: IllegalStateException) {
            //Do nothing
        }
    }
}
