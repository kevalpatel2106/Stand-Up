package com.standup.app.about.donate

import android.app.Activity
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import com.standup.app.billing.repo.MockBillingRepoImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Keval on 26/03/18.
 *
 * @author [kevalpatel2106](https : / / github.com / kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SupportDevelopmentViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val rule1 = RxSchedulersOverrideRule()

    private val test_order_id = "2367457236"

    private lateinit var model: SupportDevelopmentViewModel

    private val billingRepo = MockBillingRepoImpl()

    private lateinit var activity: Activity

    @Before
    fun setUp() {
        activity = Mockito.mock(Activity::class.java)
        model = SupportDevelopmentViewModel(billingRepo)
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.donationOrderId.value)
        Assert.assertNull(model.donationErrorCode.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_2Dollar_Success() {
        billingRepo.isError = false
        billingRepo.orderToken = test_order_id

        model.donate2Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.donationErrorCode.value)
        Assert.assertEquals(test_order_id, model.donationOrderId.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_2Dollar_Error() {
        billingRepo.isError = true
        billingRepo.orderToken = test_order_id

        model.donate2Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.donationOrderId.value)
        Assert.assertEquals(6 /* BillingClient.BillingResponse.ERROR */, model.donationErrorCode.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_5Dollar_Success() {
        billingRepo.isError = false
        billingRepo.orderToken = test_order_id

        model.donate5Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.donationErrorCode.value)
        Assert.assertEquals(test_order_id, model.donationOrderId.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_5Dollar_Error() {
        billingRepo.isError = true
        billingRepo.orderToken = test_order_id

        model.donate5Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.donationOrderId.value)
        Assert.assertEquals(6 /* BillingClient.BillingResponse.ERROR */, model.donationErrorCode.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_10Dollar_Success() {
        billingRepo.isError = false
        billingRepo.orderToken = test_order_id

        model.donate10Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.donationErrorCode.value)
        Assert.assertEquals(test_order_id, model.donationOrderId.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_10Dollar_Error() {
        billingRepo.isError = true
        billingRepo.orderToken = test_order_id

        model.donate10Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.donationOrderId.value)
        Assert.assertEquals(6 /* BillingClient.BillingResponse.ERROR */, model.donationErrorCode.value)
    }


    @Test
    @Throws(Exception::class)
    fun checkDonate_20Dollar_Success() {
        billingRepo.isError = false
        billingRepo.orderToken = test_order_id

        model.donate20Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.errorMessage.value)
        Assert.assertNull(model.donationErrorCode.value)
        Assert.assertEquals(test_order_id, model.donationOrderId.value)
    }

    @Test
    @Throws(Exception::class)
    fun checkDonate_20Dollar_Error() {
        billingRepo.isError = true
        billingRepo.orderToken = test_order_id

        model.donate20Dollar(activity)

        Assert.assertFalse(model.blockUi.value!!)
        Assert.assertNull(model.donationOrderId.value)
        Assert.assertEquals(6 /* BillingClient.BillingResponse.ERROR */, model.donationErrorCode.value)
    }

}