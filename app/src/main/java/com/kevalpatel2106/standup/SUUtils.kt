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

package com.kevalpatel2106.standup

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import com.cocosw.bottomsheet.BottomSheet
import com.kevalpatel2106.utils.Utils

/**
 * Created by Keval on 17/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
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

    fun openEmailDialog(context: Activity) {
        val bottomSheet = BottomSheet.Builder(context).title("Open mail")

        //Get the list of email clients.
        val emailAppsList = Utils.getEmailApplications(context.packageManager)

        //Add each items to the bottom sheet
        for (i in 0 until emailAppsList.size) {
            val s = emailAppsList[i]
            Utils.getApplicationName(s.activityInfo.packageName, context.packageManager)?.let {
                bottomSheet.sheet(i, s.loadIcon(context.packageManager), it)
            }
        }

        //On clicking any item, open the email application
        bottomSheet.listener { _, pos ->
            context.startActivity(context.packageManager
                    .getLaunchIntentForPackage(emailAppsList[pos].activityInfo.packageName))
        }
        bottomSheet.build()
        bottomSheet.show()
    }
}