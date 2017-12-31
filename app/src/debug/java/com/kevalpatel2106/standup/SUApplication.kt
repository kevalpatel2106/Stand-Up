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

package com.kevalpatel2106.standup

import android.os.StrictMode
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber


/**
 * Created by Keval on 13/11/17.
 *
 * Application class for the debug application. This will initialize the timber tree, strict mode
 * and shetho.
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class SUApplication : BaseSUApplication() {
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

        //Init shetho
        Stetho.initializeWithDefaults(this@SUApplication)
    }
}
