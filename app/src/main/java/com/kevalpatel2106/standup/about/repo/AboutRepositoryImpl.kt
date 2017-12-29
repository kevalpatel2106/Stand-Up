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

package com.kevalpatel2106.standup.about.repo

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class AboutRepositoryImpl : AboutRepository {

    override fun getLatestVersion(): Flowable<CheckVersionResponse> {
        return Flowable.create(object : FlowableOnSubscribe<CheckVersionResponse> {
            override fun subscribe(e: FlowableEmitter<CheckVersionResponse>) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }, BackpressureStrategy.LATEST)
    }

}