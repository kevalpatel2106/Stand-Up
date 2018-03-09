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

package com.standup.app.settings.whitelisting

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.PowerManager
import com.standup.app.settings.R
import timber.log.Timber
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.annotation.RequiresApi


/**
 * Created by Kevalpatel2106 on 09-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object WhitelistingUtils {

    @SuppressLint("NewApi")
    internal fun shouldOpenWhiteListDialog(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Timber.i("Whitelisting is not available in API < 23.")
            return false
        }

        val packageName = context.getString(R.string.package_name)
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(packageName)) {
            Timber.i("Application is already whitelisted.")
            return false
        }
        return true
    }

    @SuppressLint("InlinedApi", "BatteryLife")
    internal fun requestForWhitelisting(context: Context) {
        val intent = Intent()
        intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
        intent.data = Uri.parse("package:${context.getString(R.string.package_name)}")
        context.startActivity(intent)
    }
}
