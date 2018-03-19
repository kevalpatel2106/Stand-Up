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

package com.standup.app.billing.purchaseActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.standup.app.billing.R
import com.standup.app.billing.di.DaggerBillingComponent
import com.standup.app.billing.repo.BillingRepo
import kotlinx.android.synthetic.main.activity_purchase.*
import javax.inject.Inject

class PurchaseActivity : BaseActivity() {

    companion object {

        /**
         * Launch the [PurchaseActivity].
         */
        internal fun launch(context: Context) {
            context.startActivity(Intent(context, PurchaseActivity::class.java))
        }
    }

    @Inject
    internal lateinit var repo: BillingRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerBillingComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@PurchaseActivity)

        setContentView(R.layout.activity_purchase)

        repo.isPremiumPurchased()
                .doOnSubscribe {
                    purchase_button.isEnabled = false
                }
                .subscribe({
                    purchase_button.isEnabled = !it
                }, {
                    purchase_button.isEnabled = false
                })


        purchase_button.setOnClickListener {
            repo.purchasePremium(this@PurchaseActivity)
                    .doOnSubscribe { purchase_button.displayLoader(true) }
                    .doAfterTerminate { purchase_button.displayLoader(false) }
                    .subscribe({
                        if (it) {
                            //TODO Handle success
                        }
                    }, {
                        //TODO Display alert
                    })
        }
    }
}
