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

import com.android.billingclient.api.BillingClient
import org.junit.Assert
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.runners.Parameterized
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 23-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Enclosed::class)
class InAppFailDialogErrorMessageTest {

    @RunWith(Parameterized::class)
    class InAppFailDialogErrorMessageParameterizedTest(private val input: Int,
                                                       private val expected: Int) {


        companion object {

            @JvmStatic
            @Parameterized.Parameters
            fun data(): ArrayList<Array<out Any?>> {
                return arrayListOf(
                        arrayOf(BillingClient.BillingResponse.BILLING_UNAVAILABLE, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.DEVELOPER_ERROR, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.ERROR, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.ITEM_UNAVAILABLE, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.SERVICE_UNAVAILABLE, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.SERVICE_DISCONNECTED, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.ITEM_NOT_OWNED, R.string.in_app_purchase_error_message),
                        arrayOf(BillingClient.BillingResponse.ITEM_ALREADY_OWNED, R.string.in_app_purchase_already_owned_error_message)
                )
            }
        }

        @Test
        @Throws(IOException::class)
        fun testErrorMessage() {
            Assert.assertEquals(expected, InAppFailDialog.getErrorMessage(input))
        }

    }

    @RunWith(JUnit4::class)
    class InAppFailDialogErrorMessageNonParameterizedTest {

        @Test
        @Throws(IOException::class)
        fun testErrorMessage_ForSuccess() {
            try {
                InAppFailDialog.getErrorMessage(BillingClient.BillingResponse.OK)
                Assert.fail()
            } catch (e: IllegalStateException) {
                //Test passed
            }
        }

        @Test
        @Throws(IOException::class)
        fun testErrorMessage_ForUserCancel() {
            try {
                InAppFailDialog.getErrorMessage(BillingClient.BillingResponse.USER_CANCELED)
                Assert.fail()
            } catch (e: IllegalStateException) {
                //Test passed
            }
        }

    }
}
