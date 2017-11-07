/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.filemanager.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.support.annotation.ColorInt
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by Keval on 08-Sep-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */

object ViewUtils {

    @ColorInt
    private fun getColorAttr(context: Context, attr: Int): Int {
        val theme = context.theme
        val typedArray = theme.obtainStyledAttributes(intArrayOf(attr))
        val color = typedArray.getColor(0, Color.LTGRAY)
        typedArray.recycle()
        return color
    }

    fun toPx(context: Context, dp: Int): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics)
            .toInt()

    private fun isTablet(resources: Resources): Boolean = resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

    fun getLayoutPosition(view: View): Rect {
        val myViewRect = Rect()
        view.getGlobalVisibleRect(myViewRect)
        return myViewRect
    }

    fun showKeyboard(v: View, activity: Context) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, 0)
    }

    fun showKeyboard(v: View) = showKeyboard(v, v.context)

    fun hideKeyboard(view: View?) {
        if (view != null) {
            val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @ColorInt
    fun generateTextColor(background: Int): Int = getContrastColor(background)

    @ColorInt
    private fun getContrastColor(@ColorInt color: Int): Int {
        val a = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 *
                Color.blue(color)) / 255
        return if (a < 0.5) Color.BLACK else Color.WHITE
    }

    /**
     * @return width of your device
     */
    fun getDeviceWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    /**
     * Returns darker version of specified `color`.
     */
    fun darker(color: Int, factor: Float): Int {
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        return Color.argb(a,
                Math.max((r * factor).toInt(), 0),
                Math.max((g * factor).toInt(), 0),
                Math.max((b * factor).toInt(), 0))
    }

}

