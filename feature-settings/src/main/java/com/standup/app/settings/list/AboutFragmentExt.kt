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
import android.support.annotation.StringRes
import android.view.Gravity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction

/**
 * Created by Kevalpatel2106 on 08-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
fun prepareCard(
        @DrawableRes icon: Int,
        @StringRes text: Int,
        clickListener: MaterialAboutItemOnClickAction? = null
): MaterialAboutActionItem {
    return MaterialAboutActionItem.Builder()
            .icon(icon)
            .setIconGravity(Gravity.START)
            .text(text)
            .setOnClickAction(clickListener)
            .build()
            .setShouldShowIcon(true)
}