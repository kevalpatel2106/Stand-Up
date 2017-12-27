/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.utils

import android.support.annotation.CallSuper
import android.view.MotionEvent
import android.view.View


/**
 * Created by Keval on 19/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
open class SwipeDetector : View.OnTouchListener {

    @Suppress("MemberVisibilityCanPrivate")
    @CallSuper
    protected open fun onRightToLeftSwipe() {
        //Do nothing
    }

    @Suppress("MemberVisibilityCanPrivate")
    @CallSuper
    protected open fun onLeftToRightSwipe() {
        //Do nothing
    }

    @Suppress("MemberVisibilityCanPrivate")
    @CallSuper
    protected open fun onTopToBottomSwipe() {
        //Do nothing
    }

    @Suppress("MemberVisibilityCanPrivate")
    @CallSuper
    protected open fun onBottomToTopSwipe() {
        //Do nothing
    }


    var minSwipeDistance = 100
    private var downX: Float = 0.toFloat()
    private var downY: Float = 0.toFloat()

    private var upX: Float = 0.toFloat()
    private var upY: Float = 0.toFloat()

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }
            MotionEvent.ACTION_UP -> {
                upX = event.x
                upY = event.y

                val deltaX = downX - upX
                val deltaY = downY - upY

                if (Math.abs(deltaX) > Math.abs(deltaY)) {//HORIZONTAL SCROLL

                    //Check if the minimum horizontal swipe distance covered?
                    if (Math.abs(deltaX) > minSwipeDistance) {

                        if (deltaX < 0) {   // left or right
                            this.onLeftToRightSwipe()
                            return true
                        } else if (deltaX > 0) {//right to left
                            this.onRightToLeftSwipe()
                            return true
                        }
                    } else {
                        //not long enough swipe...
                        return false
                    }
                } else {//VERTICAL SCROLL

                    //Check if the minimum vertical swipe distance covered?
                    if (Math.abs(deltaY) > minSwipeDistance) {

                        if (deltaY < 0) {   // top to down
                            this.onTopToBottomSwipe()
                            return true
                        } else if (deltaY > 0) { //bottom to top
                            this.onBottomToTopSwipe()
                            return true
                        }
                    } else {
                        //not long enough swipe...
                        return false
                    }
                }

                return true
            }
        }
        return false
    }
}