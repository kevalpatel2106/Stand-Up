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

package com.kevalpatel2106.utils.rxbus

import com.kevalpatel2106.testutils.RxSchedulersOverrideRule
import io.reactivex.observers.TestObserver
import io.reactivex.subscribers.TestSubscriber
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/**
 * Created by Keval on 26/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class RxBusTest {
    private val TAG_1 = "tag_1"
    private val TAG_2 = "tag_2"

    @Rule
    @JvmField
    val rule = RxSchedulersOverrideRule()

    @Test
    fun checkSingleTagRegister() {
        val testObserver = TestObserver<Event>()
        RxBus.register(TAG_1).subscribe(testObserver)

        //Post event
        RxBus.post(Event(TAG_1))

        testObserver.assertValueCount(1)
                .assertValue { it.tag == TAG_1 }
                .assertNotComplete()
    }

    @Test
    fun checkExternalTagBroadcast() {
        val testObserver = TestObserver<Event>()
        RxBus.register(TAG_1).subscribe(testObserver)

        //Post event
        RxBus.post(Event(TAG_2))

        testObserver.assertNoValues().assertNotComplete()
    }

    @Test
    fun checkMultipleTagRegister() {
        val testObserver = TestObserver<Event>()
        RxBus.register(arrayOf(TAG_1, TAG_2)).subscribe(testObserver)

        //Post event
        RxBus.post(Event(TAG_1))
        RxBus.post(Event(TAG_2))

        testObserver.assertValueCount(2)
                .assertValueAt(0, { it.tag == TAG_1 })
                .assertValueAt(1, { it.tag == TAG_2 })
                .assertNotComplete()
    }
}