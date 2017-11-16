package com.kevalpatel2106.base

import android.app.Activity
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.FragmentTestRule
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

/**
 * Created by Keval on 15/11/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class BaseFragmentTest : BaseTestClass() {

    @JvmField
    @Rule
    val fragmentRule = FragmentTestRule(TestFragment::class.java)

    override fun getActivity(): Activity? = fragmentRule.activity


    @Test
    @Throws(Exception::class)
    fun checkAddDisposable() {
        Assert.assertNotNull(fragmentRule.fragment.mCompositeDisposable)

        fragmentRule.fragment.addSubscription(null)
        Assert.assertEquals(fragmentRule.fragment.mCompositeDisposable.size().toLong(), 0)

        fragmentRule.fragment.addSubscription(Observable.just("1").subscribe())
        Assert.assertEquals(fragmentRule.fragment.mCompositeDisposable.size().toLong(), 1)
    }

}