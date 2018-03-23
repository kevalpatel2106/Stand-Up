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

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import com.android.billingclient.api.BillingClient


/**
 * Created by Kevalpatel2106 on 22-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class InAppFailDialog : DialogFragment() {

    companion object {

        /**
         * Name of the argument message.
         */
        private const val ARG_MESSAGE = "arg_message"


        /**
         * Display the error dialog with proper error message whenever IAP fails. Function will return
         * true if the dialog is displayed or else it will return false.
         *
         * - This dialog will display error message based on [errorCode].
         * - If the user cancels the IAP (i.e. [errorCode] is [BillingClient.BillingResponse.USER_CANCELED]),
         * dialog won't be display.
         *
         * @see getErrorMessage
         */
        fun launch(context: Context,
                   fragmentManager: FragmentManager,
                   @BillingClient.BillingResponse errorCode: Int): Boolean {

            return when (errorCode) {
                BillingClient.BillingResponse.OK -> throw IllegalStateException("Success received.")
                BillingClient.BillingResponse.USER_CANCELED -> {
                    false
                }
                else -> {
                    InAppFailDialog().apply {
                        this.arguments = Bundle().apply {
                            this.putString(ARG_MESSAGE, context.getString(getErrorMessage(errorCode)))
                        }
                    }.show(fragmentManager, InAppFailDialog::class.java.name)
                    true
                }
            }

        }

        /**
         * Get the error message string resource id based on the [errorCode].
         *
         * @throws IllegalStateException if [errorCode] is [BillingClient.BillingResponse.USER_CANCELED]
         * or [BillingClient.BillingResponse.OK] because for those error codes, error dialog shouldn't
         * be displayed.
         */
        @VisibleForTesting
        internal fun getErrorMessage(@BillingClient.BillingResponse errorCode: Int): Int {
            return when (errorCode) {
                BillingClient.BillingResponse.OK -> {
                    throw IllegalStateException("Success received.")
                }
                BillingClient.BillingResponse.USER_CANCELED -> {
                    throw IllegalStateException("User canceled. No error message.")
                }
                BillingClient.BillingResponse.ITEM_ALREADY_OWNED -> {
                    R.string.in_app_purchase_already_owned_error_message
                }
                else -> {
                    R.string.in_app_purchase_error_message
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (arguments == null) throw IllegalStateException("Arguments cannot be null.")

        return AlertDialog.Builder(context)
                .setTitle(R.string.buy_pro_failed_error_message)
                .setMessage(arguments!!.getString(ARG_MESSAGE))
                .setPositiveButton(android.R.string.ok) { dialog, _ ->
                    dialog.dismiss()
                }
                .setNeutralButton(R.string.btn_title_faq) { dialog, _ ->

                    //Open the FAQ in browser
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.iap_troubleshoot_url))))

                    dialog.dismiss()
                }
                .create()
    }
}
