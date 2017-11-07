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

package com.kevalpatel2106.base

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Keval on 31-May-17.
 * This class is to extend the functionality of [ViewPager]. Use this class instead
 * of [ViewPager] through out the application.

 * @author 'https://github.com/kevalpatel2106'
 */

class BaseViewPager : ViewPager {

    private var mIsSwipeGestureEnable = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return this.mIsSwipeGestureEnable && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return this.mIsSwipeGestureEnable && super.onInterceptTouchEvent(event)
    }

    /**
     * @param enabled True to enable the swipe gesture for the view pager to change the currently
     * *                displaying page. By default this gesture is enabled.
     */
    fun setSwipeGestureEnable(enabled: Boolean) {
        this.mIsSwipeGestureEnable = enabled
    }
}
