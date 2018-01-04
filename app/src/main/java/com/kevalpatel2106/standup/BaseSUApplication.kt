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

package com.kevalpatel2106.standup

import android.app.Application
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.utils.SharedPrefsProvider

/**
 * Created by Keval on 31/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class BaseSUApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Initialize shared preference
        SharedPrefsProvider.init(this)

        //Initialize db
        StandUpDb.init(this@BaseSUApplication)

        //Initialize the api module
        ApiProvider.init(this@BaseSUApplication)

        //Initialize firebase.
        FirebaseApp.initializeApp(this@BaseSUApplication)

        //Initialize facebook
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this@BaseSUApplication)

        //Enable firebase analytics
        FirebaseAnalytics.getInstance(this@BaseSUApplication)
                .setAnalyticsCollectionEnabled(isReleaseBuild())
    }

    abstract fun isReleaseBuild(): Boolean
}