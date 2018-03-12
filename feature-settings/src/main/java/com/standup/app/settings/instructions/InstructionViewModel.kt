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

package com.standup.app.settings.instructions

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.standup.app.settings.R
import com.standup.app.settings.di.DaggerSettingsComponent
import javax.inject.Inject

/**
 * Created by Keval on 11/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class InstructionViewModel : BaseViewModel {

    @Inject
    internal lateinit var application: Application

    @Suppress("unused")
    constructor() {
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@InstructionViewModel)

        init()
    }

    constructor(application: Application) {
        this.application = application

        init()
    }

    internal val instructions = MutableLiveData<ArrayList<Instruction>>()


    internal fun init() {
        instructions.value = ArrayList()

        loadInstructions()
    }

    internal fun loadInstructions() {
        instructions.value?.let {
            it.clear()

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            it.add(Instruction(application.getString(R.string.title_activity_daily_review_settings),
                    application.getString(R.string.title_activity_dnd_settings),
                    R.drawable.ic_dnd_on))

            instructions.value = it
        }
    }
}