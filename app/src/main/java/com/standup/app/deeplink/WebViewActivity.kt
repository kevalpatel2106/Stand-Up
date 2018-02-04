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

package com.standup.app.deeplink

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import com.standup.R
import kotlinx.android.synthetic.main.activity_web_view.*
import kotlinx.android.synthetic.main.layout_simple_toolbar.*


class WebViewActivity : AppCompatActivity() {

    companion object {
        @VisibleForTesting
        internal val EXTRA_WEB_URL = "extra_web_url"

        /**
         * Launch web browser activity with web url
         * isNewTask to launch as new activity and clear all back activity
         */
        @JvmOverloads
        @JvmStatic
        fun launch(@NonNull context: Context,
                   @NonNull webUrl: String,
                   isNewTask: Boolean = false) {

            val launchIntent = Intent(context, WebViewActivity::class.java)
            if (isNewTask)
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK)

            launchIntent.putExtra(EXTRA_WEB_URL, webUrl)
            context.startActivity(launchIntent)
        }
    }

    @SuppressLint("RestrictedApi", "SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        //Set toolbar
        setSupportActionBar(toolbar)
        //set the title
        supportActionBar!!.title = "Browser"
        //Set the up indicator
        supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Setup web view for url
        broswerWebview.webViewClient = WebViewClient()
        broswerWebview.settings.loadsImagesAutomatically = true
        broswerWebview.settings.javaScriptEnabled = true
        broswerWebview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        broswerWebview.loadUrl(intent.getStringExtra(EXTRA_WEB_URL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            //Call on back press when home back button is clicked
            onBackPressed()
            return false
        }
        return super.onOptionsItemSelected(item)
    }
}
