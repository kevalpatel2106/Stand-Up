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

package com.standup.app.misc

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import com.cocosw.bottomsheet.BottomSheet
import com.kevalpatel2106.utils.Utils
import com.standup.R


/**
 * Created by Keval on 17/12/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object SUUtils {

    fun openLink(context: Context, url: String) {
        CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .enableUrlBarHiding()
                .setShowTitle(true)
                .build()
                .launchUrl(context, Uri.parse(url))
    }

    fun openEmailClientDialog(activity: Activity) {
        val bottomSheet = BottomSheet.Builder(activity).title(activity.getString(R.string.open_email_bottom_dialog_title))

        //Get the list of email clients.
        val emailAppsList = Utils.getEmailApplications(activity.packageManager)

        //Add each items to the bottom sheet
        for (i in 0 until emailAppsList.size) {
            val s = emailAppsList[i]
            Utils.getApplicationName(s.activityInfo.packageName, activity.packageManager)?.let {
                bottomSheet.sheet(i, s.loadIcon(activity.packageManager), it)
            }
        }

        //On clicking any item, open the email application
        bottomSheet.listener { _, pos ->
            activity.startActivity(activity.packageManager
                    .getLaunchIntentForPackage(emailAppsList[pos].activityInfo.packageName))
        }
        bottomSheet.build()
        bottomSheet.show()
    }

    fun sendEmail(activity: Activity, title: String? = null, mesaage: String? = null, to: String) {
        val bottomSheet = BottomSheet.Builder(activity).title(activity.getString(R.string.open_email_bottom_dialog_title))

        //Get the list of email clients.
        val emailAppsList = Utils.getEmailApplications(activity.packageManager)

        //Add each items to the bottom sheet
        for (i in 0 until emailAppsList.size) {
            val s = emailAppsList[i]
            Utils.getApplicationName(s.activityInfo.packageName, activity.packageManager)?.let {
                bottomSheet.sheet(i, s.loadIcon(activity.packageManager), it)
            }
        }

        //On clicking any item, open the email application
        bottomSheet.listener { _, pos ->
            val intent = Intent().apply {
                component = ComponentName(emailAppsList[pos].activityInfo.packageName, emailAppsList[pos].activityInfo.name)
                action = Intent.ACTION_SEND
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, to)
                title?.let { putExtra(Intent.EXTRA_SUBJECT, title) }
                mesaage?.let { putExtra(Intent.EXTRA_TEXT, mesaage) }
            }
            activity.startActivity(intent)
        }
        bottomSheet.build()
        bottomSheet.show()
    }
}
