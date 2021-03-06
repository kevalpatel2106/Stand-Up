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

package com.standup.app.about

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.annotation.VisibleForTesting
import com.google.android.gms.appinvite.AppInviteInvitation
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.misc.CommonUtils
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.standup.app.about.di.DaggerAboutComponent
import com.standup.app.about.donate.SupportDevelopmentActivity
import com.standup.app.about.repo.AboutRepository
import com.standup.app.about.repo.CheckVersionResponse
import com.standup.app.about.report.ReportIssueActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


/**
 * Created by Keval on 18/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class AboutViewModel : BaseViewModel {
    val REQUEST_CODE_INVITE = 6123

    /**
     * Repository to provide user authentications.
     */
    @Inject
    lateinit var authRepository: AboutRepository

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param authRepository Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(authRepository: AboutRepository) : super() {
        this.authRepository = authRepository

        init()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerAboutComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@AboutViewModel)

        init()
    }

    internal val isCheckingUpdate = MutableLiveData<Boolean>()

    internal val versionUpdateResult = MutableLiveData<CheckVersionResponse>()

    fun init() {
        isCheckingUpdate.value = false
    }

    fun handleRateUs(context: Context) {
        try {
            val uri = Uri.parse("market://details?id=" + context.packageName)

            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            context.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            AboutUtils.openLink(context, context.getString(R.string.rate_app_url))
        }
    }

    fun checkForUpdate() {
        authRepository.getLatestVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { isCheckingUpdate.value = true }
                .doOnTerminate { isCheckingUpdate.value = false }
                .subscribe({
                    it?.let { versionUpdateResult.value = it }
                }, {
                    versionUpdateResult.value = null
                    errorMessage.value = ErrorMessage(R.string.check_update_error_message)
                })
    }

    fun handleReportIssue(context: Context) {
        ReportIssueActivity.launch(context)
    }


    fun handleSupportDevelopment(context: Context) {
        SupportDevelopmentActivity.launch(context)
    }

    fun handleShareWithFriends(activity: Activity) {
        val intent = AppInviteInvitation.IntentBuilder(activity.getString(R.string.invitation_title))
                .setMessage(activity.getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(activity.getString(R.string.invitation_deep_link)))
                .setCallToActionText(activity.getString(R.string.invitation_cta))
                .build()
        activity.startActivityForResult(intent, REQUEST_CODE_INVITE)
    }

    fun handleOpenSourceLibs(context: Context) {
        LibsBuilder().withActivityStyle(Libs.ActivityStyle.DARK)
                .withActivityTitle("We  ♡  Open source")
                .withAutoDetect(true)
                .withAboutIconShown(true)
                .withAboutVersionShownName(true)
                .withAboutVersionShownCode(false)
                .withLicenseShown(true)
                .start(context)
    }

    fun handleJoinSlack(context: Context) {
        AboutUtils.openLink(context, context.getString(R.string.join_slack_url))
    }

    fun handleSupportEmail(activity: Activity) {
        CommonUtils.sendEmail(activity = activity, to = activity.getString(R.string.support_email))
    }

    fun handleFollowProjectTwitter(context: Context) {
        AboutUtils.openLink(context, context.getString(R.string.join_stand_up_twitter))
    }

    fun handleFollowAuthorTwitter(context: Context) {
        AboutUtils.openLink(context, context.getString(R.string.author_twitter_profile_link))
    }

    fun handleAuthorProfile(context: Context) {
        AboutUtils.openLink(context, context.getString(R.string.author_twitter_profile_link))
    }

    fun handelFollowAuthorOnGitHub(context: Context) {
        AboutUtils.openLink(context, context.getString(R.string.author_github))
    }
}
