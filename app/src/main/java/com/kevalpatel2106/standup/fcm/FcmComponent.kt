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

package com.kevalpatel2106.standup.fcm

import com.kevalpatel2106.standup.misc.di.AppComponent
import com.kevalpatel2106.standup.misc.di.AppScope
import com.kevalpatel2106.standup.misc.di.PrefModule
import dagger.Component

/**
 * Created by Keval on 09/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@AppScope
@Component(dependencies = [AppComponent::class], modules = [PrefModule::class])
interface FcmComponent{

    fun inject(fcmMessagingService: FcmMessagingService)

    fun inject(instanceIdService: InstanceIdService)
}