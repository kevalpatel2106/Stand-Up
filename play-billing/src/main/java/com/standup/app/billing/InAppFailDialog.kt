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
         * Launch the [InAppFailDialog] which displays [message].
         */
        fun launch(fragmentManager: FragmentManager, message: String) {
            InAppFailDialog().apply {
                this.arguments = Bundle().apply {
                    this.putString(ARG_MESSAGE, message)
                }
            }.show(fragmentManager, InAppFailDialog::class.java.name)
        }


        fun launch(context: Context,
                   fragmentManager: FragmentManager,
                   @BillingClient.BillingResponse errorCode: Int) {

            when (errorCode) {
                BillingClient.BillingResponse.OK -> throw IllegalStateException("Success received.")
                BillingClient.BillingResponse.USER_CANCELED -> {
                    /* Do nothing */
                }
                else -> {
                    launch(fragmentManager, context.getString(getErrorMessage(errorCode)))
                }
            }

        }

        private fun getErrorMessage(@BillingClient.BillingResponse errorCode: Int): Int {
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
