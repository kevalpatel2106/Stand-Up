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

package com.standup.app.settings.list

import android.support.annotation.DrawableRes

/**
 * Created by Keval on 11/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class SettingsItem(
        var id: SettingsId,

        val title: String,

        @DrawableRes
        val icon: Int
) {

    var isSelected: Boolean = false

    init {
        if (title.isEmpty()) throw IllegalArgumentException("Title cannot be null or empty.")
    }
}
