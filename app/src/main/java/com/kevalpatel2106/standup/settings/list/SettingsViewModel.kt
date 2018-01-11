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

package com.kevalpatel2106.standup.settings.list

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.application.BaseApplication
import com.kevalpatel2106.standup.settings.di.DaggerSettingsComponent
import com.kevalpatel2106.utils.SharedPrefsProvider
import java.util.*
import javax.inject.Inject

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SettingsViewModel : BaseViewModel {

    @Inject lateinit var sharedPrefsProvider: SharedPrefsProvider

    @VisibleForTesting
    constructor(sharedPrefsProvider: SharedPrefsProvider) {
        this.sharedPrefsProvider = sharedPrefsProvider
        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.appComponent)
                .build()
                .inject(this@SettingsViewModel)
        init()
    }

    internal val settingsItemList = MutableLiveData<Array<SettingsItem>>()

    fun init() {
        settingsItemList.value = null
    }

    internal fun prepareSettingsList() {
        val arrayList = ArrayList<SettingsItem>()

        //Add sync
        arrayList.add(SettingsItem("Sync", R.drawable.ic_sync))

        //Add DND
        arrayList.add(SettingsItem("DND", R.drawable.ic_dnd_on))

        //Add Notifications
        arrayList.add(SettingsItem("Notifications", R.drawable.ic_notifications_bell))

        //Add Sleep hour
        arrayList.add(SettingsItem("Sleep hours", R.drawable.ic_sleeping))

        //Add Sleep hour
        arrayList.add(SettingsItem("Logout", R.drawable.ic_logout))

        //Add Sleep hour
        arrayList.add(SettingsItem("Privacy Policy", R.drawable.ic_privacy_policy))

        settingsItemList.value = arrayList.toArray(arrayOf<SettingsItem>())
    }
}
