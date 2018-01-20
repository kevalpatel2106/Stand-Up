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

package com.kevalpatel2106.common.application

import android.app.Application
import com.kevalpatel2106.common.application.di.AppComponent
import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.application.di.DaggerAppComponent

/**
 * Created by Keval on 31/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class BaseApplication : Application() {


    companion object {
        private lateinit var appComponent: AppComponent

        @JvmStatic
        fun getApplicationComponent(): AppComponent {
            return appComponent
        }
    }

    override fun onCreate() {
        super.onCreate()

        //Inject dagger
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this@BaseApplication, baseUrl()))
                .build()
        appComponent.inject(this@BaseApplication)
    }

    abstract fun isReleaseBuild(): Boolean

    abstract fun baseUrl(): String
}
