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

package com.standup.app

import android.os.StrictMode
import com.facebook.FacebookSdk
import com.google.firebase.FirebaseApp
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.prefs.UserSessionManager
import com.kevalpatel2106.common.prefs.UserSettingsManager
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.squareup.leakcanary.LeakCanary
import com.standup.core.Core
import timber.log.Timber


/**
 * Created by Keval on 13/11/17.
 *
 * Application class for the debug application. This will initialize the timber tree, strict mode
 * and shetho.
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class SUApplication : BaseApplication() {

    override fun baseUrl(): String = BuildConfig.BASE_URL

    override fun isReleaseBuild(): Boolean = false

    override fun onCreate() {
        super.onCreate()

        //Enable strict mode
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        //Enable timber
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return (super.createStackElementTag(element) + ":" + element.methodName
                        + " ->L" + element.lineNumber)
            }
        })

        //Initialize the leak canary
        if (LeakCanary.isInAnalyzerProcess(this@SUApplication)) return
        LeakCanary.install(this@SUApplication)

        //Initialize firebase.
        FirebaseApp.initializeApp(this@SUApplication)

        //Initialize facebook
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this@SUApplication)

        val prefProvider = SharedPrefsProvider(this@SUApplication)
        Core(UserSessionManager(prefProvider), UserSettingsManager(prefProvider), prefProvider)
                .turnOn(this@SUApplication)
    }
}
