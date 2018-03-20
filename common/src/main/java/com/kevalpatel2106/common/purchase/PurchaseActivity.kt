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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.kevalpatel2106.common.R
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showSnack
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

    internal lateinit var model: PurchaseViewModel

    @SuppressLint("VisibleForTests")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        model = ViewModelProviders.of(this@PurchaseActivity).get(PurchaseViewModel::class.java)
        model.blockUi.observe(this@PurchaseActivity, Observer {
            it?.let { purchase_button.displayLoader(it) }
        })
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
        model.isPremiumPurchased.observe(this@PurchaseActivity, Observer {
            it?.let { purchase_button.isEnabled = !it }
        })

        purchase_button.setOnClickListener {
            model.buyPro(this@PurchaseActivity)
        }
    }
}
