package com.kevalpatel2106.standup.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.MenuItem
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.google.android.gms.appinvite.AppInviteInvitation
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.SUUtils
import com.kevalpatel2106.standup.constants.AnalyticsEvents
import com.kevalpatel2106.standup.constants.logEvent
import com.kevalpatel2106.utils.showSnack
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder


class AboutActivity : MaterialAboutActivity() {
    private val REQUEST_INVITE = 6123

    companion object {

        /**
         * Launch the [AboutActivity] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, AboutActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    override fun getActivityTitle(): CharSequence = getString(R.string.application_name)

    override fun getMaterialAboutList(p0: Context): MaterialAboutList {
        return MaterialAboutList(getAboutCard(),
                getSupportUsCard(),
                getJoinUsCard(),
                getAuthorCard()
        )
    }

    private fun getSupportUsCard(): MaterialAboutCard {
        //Rate
        val rateUsItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_star_orange)
                .setIconGravity(Gravity.START)
                .text("Rate this app")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.rate_app_url))
                })
                .build()

        //Issue
        val reportIssueItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_bug_report_brown)
                .setIconGravity(Gravity.START)
                .text("Report an issue")
                .subText("Are you facing any issue? Report it here.")
                .setOnClickAction({
                    //TODO Open report issue page
                })
                .build()

        //Donate
        val donateItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_heart_fill_red)
                .setIconGravity(Gravity.START)
                .text("Support Development")
                .setOnClickAction({
                    //TODO open support PayPal
                })
                .build()

        //Share
        val shareItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_share_blue)
                .setIconGravity(Gravity.START)
                .text("Share with friends")
                .setOnClickAction({
                    val intent = AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                            .setCallToActionText(getString(R.string.invitation_cta))
                            .build()
                    startActivityForResult(intent, REQUEST_INVITE)
                })
                .build()

        return MaterialAboutCard.Builder()
                .title("Support Us")
                .addItem(rateUsItem)
                .addItem(reportIssueItem)
                .addItem(donateItem)
                .addItem(shareItem)
                .build()
    }

    private fun getAboutCard(): MaterialAboutCard {
        //Version
        val versionItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_version_white)
                .setIconGravity(Gravity.START)
                .text("Version")
                .subText(BuildConfig.VERSION_NAME)
                .setOnClickAction({
                    val snackbar = showSnack(getString(R.string.about_check_for_update), Snackbar.LENGTH_INDEFINITE)

                    //TODO Check for the version upgrade
                    Handler().postDelayed({ snackbar.dismiss() }, 6000L)
                })
                .build()

        //open source libs
        val openSourceLibsItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_github_white)
                .setIconGravity(Gravity.START)
                .text("Open source libraries")
                .setOnClickAction({
                    LibsBuilder().withActivityStyle(Libs.ActivityStyle.DARK)
                            .withActivityTitle("We  ♡  Open source")
                            .withAutoDetect(true)
                            .withAboutIconShown(true)
                            .withAboutVersionShownName(true)
                            .withAboutVersionShownCode(false)
                            .withLicenseShown(true)
                            .start(this@AboutActivity)
                })
                .build()

        //change log
        //TODO Add change logs
        /*val changelogItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_changelog)
                .setIconGravity(Gravity.START)
                .text("Changelog")
                .setOnClickAction({
                    //TODO Show change logs
                })
                .build()*/

        return MaterialAboutCard.Builder()
                .title("About")
                .addItem(versionItem)
                .addItem(openSourceLibsItem)
//                .addItem(changelogItem)
                .build()
    }


    private fun getAuthorCard(): MaterialAboutCard {
        //Name
        val authName = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_person_white)
                .setIconGravity(Gravity.START)
                .text("Keval Patel")
                .subText("kevalpatel2106")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.author_profile_link))
                })
                .build()

        //Github
        val githubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_github_white)
                .setIconGravity(Gravity.START)
                .text("Follow on GitHub")
                .subText("github.com/kevalpatel2106")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.author_github))
                })
                .build()


        //Email
        val emailItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_email_white)
                .setIconGravity(Gravity.START)
                .text("Send an Email")
                .subText("kevalpatel2106@gmail.com")
                .setOnClickAction({
                    SUUtils.onOpenEmail(this@AboutActivity)
                })
                .build()


        return MaterialAboutCard.Builder()
                .title("Author")
                .addItem(authName)
                .addItem(githubItem)
                .addItem(emailItem)
                .build()
    }

    private fun getJoinUsCard(): MaterialAboutCard {
        //Slack
        val slackItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_slack)
                .setIconGravity(Gravity.START)
                .text("Join Slack")
                .subText("https://stand-up-opensource.slack.com")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.join_slack_url))
                })
                .build()

        //Twitter
        val twitterItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_twitter)
                .setIconGravity(Gravity.START)
                .text("Follow on Twitter")
                .subText("@kevalpatel2106")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.join_stand_up_twitter))
                })
                .build()

        //Fork on GitHub
        val githubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_fork)
                .setIconGravity(Gravity.START)
                .text("Fork on GitHub")
                .setOnClickAction({
                    SUUtils.openLink(this@AboutActivity, getString(R.string.fork_repo_url))
                })
                .build()

        return MaterialAboutCard.Builder()
                .title("Connect with us")
                .addItem(slackItem)
                .addItem(twitterItem)
                .addItem(githubItem)
                .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return false//override
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                val ids = AppInviteInvitation.getInvitationIds(resultCode, data)

                //Log analytics
                val bundle = Bundle()
                bundle.putStringArray("invite_ids", ids)
                logEvent(AnalyticsEvents.EVENT_APP_INVITE_SUCCESS, bundle)
            } else {
                // Sending failed or it was canceled, show failure message to the user
                //Log analytics
                logEvent(AnalyticsEvents.EVENT_APP_INVITE_CANCEL)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
