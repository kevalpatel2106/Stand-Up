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

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Predicate
import io.reactivex.subjects.PublishSubject

/**
 * Created by Keval on 06-Jul-17.
 * This is rx sBus to pass messages between different layers of the application.
 *
 * @author [&#39;https://github.com/kevalpatel2106&#39;]['https://github.com/kevalpatel2106']
 */

object RxBus {
    private val sBus = PublishSubject.create<Event>()

    /**
     * Post an event to the RxBus.
     *
     * @param event [Event] to post.
     */
    fun post(event: Event) = sBus.onNext(event)

    fun register(tag: String): Observable<Event> = register(arrayOf(tag))

    fun register(tags: Array<String>): Observable<Event> {
        return sBus.filter(Predicate { (tag) ->
            tags.filter { tag == it }.forEach { return@Predicate true }
            false
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread())
    }
}
