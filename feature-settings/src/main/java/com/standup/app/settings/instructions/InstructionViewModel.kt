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
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
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

    @Suppress("unused")
    constructor(application: Application) {
        this.application = application

        init()
    }

    internal val instructions = MutableLiveData<ArrayList<Instruction>>()


    internal fun init() {
        instructions.value = ArrayList()

        loadInstructions()
    }

    @VisibleForTesting
    internal fun loadInstructions() {
        instructions.value!!.let {
            it.clear()

            it.add(Instruction(application.getString(R.string.instruction_title_1),
                    application.getString(R.string.instruction_message_1),
                    R.drawable.ic_intro_how_to))

            it.add(Instruction(application.getString(R.string.instruction_title_2),
                    application.getString(R.string.instruction_message_2),
                    R.drawable.ic_intro_drive_steps))

            it.add(Instruction(application.getString(R.string.instruction_title_3),
                    application.getString(R.string.instruction_message_3),
                    R.drawable.ic_intro_accuracy))

            it.add(Instruction(application.getString(R.string.instruction_title_4),
                    application.getString(R.string.instruction_message_4),
                    R.drawable.ic_intro_placement_suggestion))

            it.add(Instruction(application.getString(R.string.instruction_title_5),
                    application.getString(R.string.instruction_message_5),
                    R.drawable.ic_intro_battery_saving))

            it.add(Instruction(application.getString(R.string.instruction_title_6),
                    application.getString(R.string.instruction_message_6),
                    R.drawable.ic_intro_stops_counting))

            instructions.value = it
        }
    }
}
