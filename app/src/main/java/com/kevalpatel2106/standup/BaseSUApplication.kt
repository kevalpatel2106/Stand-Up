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
import com.kevalpatel2106.standup.misc.di.AppComponent
import com.kevalpatel2106.standup.misc.di.AppModule
import com.kevalpatel2106.standup.misc.di.DaggerAppComponent
import com.kevalpatel2106.standup.misc.di.NetworkModule

/**
 * Created by Keval on 31/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class BaseSUApplication : Application() {


    companion object {
        private lateinit var appComponent: AppComponent

        @JvmStatic
        fun getApplicationComponent(): AppComponent {
            return appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .networkModule(NetworkModule(BuildConfig.BASE_URL))
                .build()

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
