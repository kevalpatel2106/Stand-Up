package com.kevalpatel2106.base.arch

import android.arch.core.executor.testing.InstantTaskExecutorRule
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Created by Keval on 26/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

@RunWith(JUnit4::class)
class BaseViewModelTest {
    lateinit var testViewModel: TestViewModel

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        testViewModel = TestViewModel()
    }

    @Test
    @Throws(IOException::class)
    fun checkInitOfCompositeDisposable() {
        Assert.assertNotNull(testViewModel.mCompositeDisposable)
        Assert.assertEquals(testViewModel.mCompositeDisposable.size(), 0)
    }


    @Test
    @Throws(IOException::class)
    fun checkInitOfBlockUi() {
        Assert.assertNotNull(testViewModel.blockUi.value)
        Assert.assertFalse(testViewModel.blockUi.value!!)
    }


    @Test
    @Throws(IOException::class)
    fun checkInitOfErrorMessage() {
        Assert.assertNull(testViewModel.errorMessage.value)
    }

    @Test
    @Throws(IOException::class)
    fun checkOnClear() {
        testViewModel.addDisp(Observable.timer(10, TimeUnit.SECONDS).subscribe())
        testViewModel.clear()

        Assert.assertEquals(testViewModel.mCompositeDisposable.size(), 0)
    }

    @Test
    @Throws(IOException::class)
    fun checkAddDisposable() {
        Assert.assertEquals(testViewModel.mCompositeDisposable.size(), 0)

        testViewModel.addDisp(Observable.timer(10, TimeUnit.SECONDS).subscribe())
        Assert.assertEquals(testViewModel.mCompositeDisposable.size(), 1)
    }
}