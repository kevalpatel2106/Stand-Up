/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.base.arch

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SingleLiveEventTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Test
    fun checkIfOnChangeCallsOnSettingValueAfterRegisteringObserve() {
        val testValue = 5
        val singleData = SingleLiveEvent<Int>()

        singleData.observeForever { Assert.assertEquals(it, testValue) }

        singleData.value = testValue
    }

    @Test
    fun checkIfOnChangeCallsRegisteringObserveAfterSettingValue() {
        val testValue = 5
        val singleData = SingleLiveEvent<Int>()

        //Register for the first time
        var observer = Observer<Int> { Assert.assertEquals(it, testValue) }
        singleData.observeForever(observer)
        singleData.value = testValue

        //Now remove it
        singleData.removeObserver(observer)

        //Register again.
        observer = Observer {
            //Single event should not call the observe method if the new observer gets register it
            //want call the onChange.
            Assert.fail()
        }
        singleData.observeForever(observer)
    }

    @Test
    fun checkCallDoesNotTriggersOnChange() {
        val singleData = SingleLiveEvent<Int>()

        //Register for the first time
        val observer = Observer<Int> { Assert.fail() }
        singleData.observeForever(observer)
        singleData.call()
    }
}