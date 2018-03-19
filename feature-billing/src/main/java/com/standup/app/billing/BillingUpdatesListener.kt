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

import com.android.billingclient.api.Purchase

/**
 * Created by Keval on 17/03/18.
 *
 * Listener to the updates that happen when purchases list was updated or consumption of the
 * item was finished.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal interface BillingUpdatesListener {

    /**
     * This callback will be called when ever the purchased items are updated. [purchases] contains
     * the list of [Purchase].
     */
    fun onPurchasesUpdated(purchases: List<Purchase>)

    /**
     * Callback when error occurred.
     */
    fun onError(errorCode: Int)
}
