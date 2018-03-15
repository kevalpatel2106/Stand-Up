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
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.base.uiController.showToast
import com.standup.app.authentication.AuthenticationApi
import com.standup.app.splash.SplashActivity
import dagger.Lazy
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 27-Nov-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class DeepLinkActivity : BaseActivity() {

    @Inject
    internal lateinit var mAuthenticationApi: Lazy<AuthenticationApi>

    internal lateinit var model: DeepLinkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerDeepLinkComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DeepLinkActivity)

        setViewModel()
        onNewIntent(intent)
    }

    private fun setViewModel() {
        model = ViewModelProviders.of(this@DeepLinkActivity).get(DeepLinkViewModel::class.java)

        model.errorMessage.observe(this@DeepLinkActivity, Observer {
            it?.let { showToast(it.getMessage(this@DeepLinkActivity)!!) }
        })

        model.fromInvitationLink.observe(this@DeepLinkActivity, Observer {
            it?.let {
                if (!it) return@let

                //Firebase app invite link
                //Open the splash
                startActivity(SplashActivity.getLaunchIntent(this@DeepLinkActivity))
                finish()
            }
        })

        model.verifyEmailLink.observe(this@DeepLinkActivity, Observer<String> {
            it?.let {
                mAuthenticationApi.get().verifyEmailLink(this@DeepLinkActivity, it)
                finish()
            }
        })

        model.forgotPasswordLink.observe(this@DeepLinkActivity, Observer<String> {
            it?.let {
                openLink(it)
                finish()
            }
        })

        model.otherLink.observe(this@DeepLinkActivity, Observer<String> {
            it?.let {
                openLink(it)
                finish()
            }
        })
    }

    @SuppressLint("VisibleForTests")
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        //Check if the user is logged in?
        if (!model.checkIfDeepLinkAllowed()) {
            startActivity(SplashActivity.getLaunchIntent(this@DeepLinkActivity))
            return
        }

        model.processIncomingLink(this@DeepLinkActivity, intent.data.toString())
    }

    private fun openLink(linkToOpen: String) {
        WebViewActivity.launch(this@DeepLinkActivity, linkToOpen, false)
    }

}
