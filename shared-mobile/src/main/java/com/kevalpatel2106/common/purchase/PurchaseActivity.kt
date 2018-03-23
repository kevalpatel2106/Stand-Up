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

package com.kevalpatel2106.common.purchase

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kevalpatel2106.common.R
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.base.uiController.showToast
import com.kevalpatel2106.utils.alert
import com.standup.app.billing.InAppFailDialog
import kotlinx.android.synthetic.main.activity_purchase.*

class PurchaseActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [PurchaseActivity].
         */
        fun launch(context: Context) {
            context.startActivity(Intent(context, PurchaseActivity::class.java))
        }
    }

    /**
     * Instance of the [PurchaseViewModel].
     */
    internal lateinit var model: PurchaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        setToolbar(R.id.include, R.string.title_purchase_activity, true)

        setViewModel()

        //Purchase title
        premium_title_tv.text = getString(R.string.premium_features_title)

        //Purchase message
        premium_message_tv.text = getString(R.string.premium_features_list)

        //Purchase button.
        purchase_button.setOnClickListener {
            model.buyPro(this@PurchaseActivity)
        }
    }

    /**
     * Set up [PurchaseViewModel].
     */
    private fun setViewModel() {
        model = ViewModelProviders.of(this@PurchaseActivity).get(PurchaseViewModel::class.java)

        //Display the progressbar if the purchase is in progress
        model.blockUi.observe(this@PurchaseActivity, Observer {
            it?.let { purchase_button.displayLoader(it) }
        })

        //Display the error message in the alert dialog
        model.errorMessage.observe(this@PurchaseActivity, Observer {
            it?.let {
                @Suppress("ObjectLiteralToLambda")
                showSnack(
                        message = it.errorMessage!!,
                        actionName = getString(it.getErrorBtnText()),
                        actionListener = object : View.OnClickListener {
                            override fun onClick(v: View?) {
                                it.getOnErrorClick()?.invoke()
                            }
                        }
                )
            }
        })

        //Change the state of the buy button if the premium button is visible
        model.isPremiumPurchased.observe(this@PurchaseActivity, Observer {
            it?.let { purchase_button.visibility = if (it) View.GONE else View.VISIBLE }
        })

        //Handle the buy premium success.
        model.premiumOrderId.observe(this@PurchaseActivity, Observer {
            it?.let {

                alert(titleResource = R.string.buy_pro_success_title,
                        messageResource = R.string.buy_pro_success_message,
                        func = {
                            positiveButton(android.R.string.ok, {
                                //Do nothing
                                finish()
                            })
                            negativeButton(android.R.string.copy, {
                                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Order id:", it)
                                clipboard.primaryClip = clip

                                showToast(R.string.copied_to_clip_board)

                                finish()
                            })
                        }
                ).show()
            }
        })

        //Handle the error while purchasing
        model.buyPremiumErrorCode.observe(this@PurchaseActivity, Observer {
            it?.let {
                InAppFailDialog.launch(
                        context = this@PurchaseActivity,
                        fragmentManager = supportFragmentManager,
                        errorCode = it
                )
            }
        })
    }

    /**
     * @see https://stackoverflow.com/a/10261449/4690731
     */
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
        //No call for super(). Bug on API Level > 11.
    }
}
