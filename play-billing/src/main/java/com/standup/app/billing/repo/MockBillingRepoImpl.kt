package com.standup.app.billing.repo

import android.app.Activity
import android.app.Application
import com.android.billingclient.api.BillingClient
import com.standup.app.billing.IAPException
import io.reactivex.Single

/**
 * Created by Kevalpatel2106 on 21-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class MockBillingRepoImpl : BillingRepo {

    var isError: Boolean = false
    var orderToken: String = ""

    var isPremiumPurchased: Boolean = true
    override fun isPremiumPurchased(application: Application): Single<Boolean> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(isPremiumPurchased)
            }
        }
    }

    override fun purchasePremium(activity: Activity): Single<String> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }


    override fun donate2Dollar(activity: Activity): Single<String> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }

    override fun donate5Dollar(activity: Activity): Single<String> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }

    override fun donate10Dollar(activity: Activity): Single<String> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }

    override fun donate20Dollar(activity: Activity): Single<String> {
        return Single.create {
            if (isError) {
                it.onError(IAPException(BillingClient.BillingResponse.ERROR))
            } else {
                it.onSuccess(orderToken)
            }
        }
    }

}
