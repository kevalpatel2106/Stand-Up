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

package com.kevalpatel2106.utils

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.Display


/**
 * Created by Keval on 04/01/18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

@SuppressLint("MissingPermission")
fun Context.vibrate(mills: Long) {
    val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(mills, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(mills)
        }
    }
}

@Suppress("DEPRECATION")
@SuppressLint("ObsoleteSdkInt")
fun Context.isScreenOn(): Boolean {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        val dm = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        dm.displays.any { it.state != Display.STATE_OFF }
    } else {
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        powerManager.isScreenOn
    }
}

@Suppress("DEPRECATION")
@SuppressLint("ObsoleteSdkInt")
fun Context.isDeviceLocked(): Boolean {
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.inKeyguardRestrictedInputMode()
}
