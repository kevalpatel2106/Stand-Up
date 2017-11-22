package com.kevalpatel2106.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import java.util.concurrent.TimeUnit


/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object Utils {

    /**
     * Get current device id.
     *
     * @param context instance of caller.
     * @return Device unique id.
     */
    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * Get the device model name.
     *
     * @return Device model (e.g. Samsung Galaxy S4)
     */
    fun getDeviceName(): String = String.format("%s-%s", Build.MANUFACTURER, Build.MODEL)


    fun convertToNano(timeInMills: Long): Long = TimeUnit.MILLISECONDS.toNanos(timeInMills)

    fun convertToMilli(timeInNano: Long): Long = TimeUnit.NANOSECONDS.toMillis(timeInNano)
}