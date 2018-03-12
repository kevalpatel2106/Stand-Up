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

/**
 * Created by Keval on 11/03/18.
 * Instruction POJO to display the instructions in [InstructionActivity].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
data class Instruction(

        /**
         * Title of the instruction. (e.g. How the activity recognition works?)
         */
        val heading: String,

        /**
         * Message or explanation of the instruction. (e.g. It uses the accelorometer to detect the
         * motion and tracks your activity.)
         */
        val message: String,

        /**
         * Icon to display besides of the [heading]
         */
        val icon: Int
)