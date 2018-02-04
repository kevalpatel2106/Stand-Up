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

package com.standup.app.settings

import android.content.Context
import com.kevalpatel2106.utils.getColorCompat
import com.philliphsu.bottomsheetpickers.time.grid.GridTimePickerDialog
import com.standup.R

/**
 * Created by Keval on 25/01/18.
 * List of all the extention methods for the [GridTimePickerDialog].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

/**
 * Set the theme parameters and colors for [GridTimePickerDialog].
 *
 * @see GridTimePickerDialog
 */
fun GridTimePickerDialog.setApplicationTheme(context: Context) {
    setAmPmTextColorSelected(context.getColorCompat(R.color.colorPrimaryText))
    setAmPmTextColorUnselected(context.getColorCompat(R.color.colorPrimaryLight))
    setHeaderTextColorSelected(context.getColorCompat(R.color.colorPrimaryText))
    setHeaderTextColorUnselected(context.getColorCompat(R.color.colorPrimaryLight))
    setTimeSeparatorColor(context.getColorCompat(R.color.colorAccent))

    setHeaderTextDark(false)
    setAccentColor(context.getColorCompat(R.color.colorPrimary))
    setHeaderColor(context.getColorCompat(R.color.colorPrimary))
    setBackgroundColor(context.getColorCompat(R.color.colorWindowBackground))
    tryVibrate()
    isThemeDark = true
}