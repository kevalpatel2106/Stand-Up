package com.kevalpatel2106.standup.about

import android.app.Activity
import android.arch.lifecycle.MutableLiveData
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.github.javiersantos.appupdater.AppUpdaterUtils
import com.github.javiersantos.appupdater.enums.AppUpdaterError
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.github.javiersantos.appupdater.objects.Update
import com.google.android.gms.appinvite.AppInviteInvitation
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.standup.about.donate.SupportDevelopmentActivity
import com.kevalpatel2106.standup.about.report.ReportIssueActivity
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder


/**
 * Created by Keval on 18/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class AboutViewModel : BaseViewModel() {
    val REQUEST_CODE_INVITE = 6123

    internal val isCheckingUpdate = MutableLiveData<Boolean>()

    internal val versionUpdateResult = MutableLiveData<Update>()

    init {
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
            SUUtils.openLink(context, context.getString(R.string.rate_app_url))
        }
    }

    fun checkForUpdate(context: Context) {
        isCheckingUpdate.value = true

        //Check application updates.
        AppUpdaterUtils(context.applicationContext)
                .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                .withListener(object : AppUpdaterUtils.UpdateListener {
                    override fun onSuccess(update: Update?, isUpdateAvailable: Boolean) {
                        isCheckingUpdate.value = false

                        if (isUpdateAvailable) {
                            //Update available
                            update?.let { versionUpdateResult.value = update }
                        } else {
                            //No update available
                            //Do nothing
                            versionUpdateResult.value = null
                        }
                    }

                    override fun onFailed(error: AppUpdaterError?) {
                        isCheckingUpdate.value = false
                        errorMessage.value = ErrorMessage(R.string.check_update_error_message)
                        versionUpdateResult.value = null
                    }

                })
                .start()
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

    fun handleOpenGitHubProject(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.github_project_link))
    }

    fun handleJoinSlack(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.join_slack_url))
    }

    fun handleFollowProjectTwitter(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.join_stand_up_twitter))
    }

    fun handleForkOnGitHub(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.fork_repo_url))
    }

    fun handleAuthorProfile(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.author_profile_link))
    }

    fun handelFollowAuthorOnGitHub(context: Context) {
        SUUtils.openLink(context, context.getString(R.string.author_github))
    }

    fun handleAuthorEmail(activity: Activity) {
        SUUtils.onOpenEmail(activity)
    }
}