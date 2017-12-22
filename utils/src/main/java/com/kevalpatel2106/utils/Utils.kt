package com.kevalpatel2106.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import java.math.BigDecimal


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
    fun getDeviceId(context: Context): String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * Get the device model name.
     *
     * @return Device model (e.g. Samsung Galaxy S4)
     */
    fun getDeviceName(): String = String.format("%s-%s", Build.MANUFACTURER, Build.MODEL)

    fun getApplicationName(packageName: String, packageManager: PackageManager): String? {
        val ai: ApplicationInfo? = try {
            packageManager.getApplicationInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return if (ai != null) packageManager.getApplicationLabel(ai).toString() else null
    }

    fun getEmailApplications(packageManager: PackageManager): List<ResolveInfo> {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        return packageManager.queryIntentActivities(emailIntent, 0)
    }

    /**
     * Calculates the percentage from the [value] and the [total] value. Both [value] must
     * be non negative numbers. [total] must be > 0.
     */
    fun calculatePercent(value: Long, total: Long): Double {
        if (value < 0) throw IllegalArgumentException("Values cannot be negative.")
        if (total <= 0) throw IllegalArgumentException("Total cannot be negative or zero.")

        val result = (value.toDouble() / total.toDouble()) * 100
        return convertToTwoDecimal(result)
    }

    fun convertToTwoDecimal(value: Double): Double {
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}