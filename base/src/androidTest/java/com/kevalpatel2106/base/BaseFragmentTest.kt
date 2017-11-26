package com.kevalpatel2106.base

import android.app.Activity
import com.kevalpatel2106.testutils.BaseTestClass
import com.kevalpatel2106.testutils.FragmentTestRule
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

/**
 * Created by Keval on 15/11/17.
 *
 * @author 'https://github.com/kevalpatel2106'
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

        fragmentRule.fragment.addSubscription(Observable.timer(10, TimeUnit.SECONDS).subscribe())
        Assert.assertEquals(fragmentRule.fragment.mCompositeDisposable.size(), 1)

        fragmentRule.fragment.mCompositeDisposable.dispose()

        fragmentRule.fragment.addSubscription(null)
        Assert.assertEquals(fragmentRule.fragment.mCompositeDisposable.size(), 0)
    }

}