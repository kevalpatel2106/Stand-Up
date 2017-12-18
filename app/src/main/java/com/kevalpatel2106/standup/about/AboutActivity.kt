package com.kevalpatel2106.standup.about

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Gravity
import android.view.MenuItem
import android.view.View
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


class AboutActivity : MaterialAboutActivity() {

    private lateinit var model: AboutViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProviders.of(this@AboutActivity).get(AboutViewModel::class.java)

        var snackbar: Snackbar? = null
        model.isCheckingUpdate.observe(this@AboutActivity, Observer {
            if (it!!) {
                snackbar = showSnack(R.string.about_check_for_update, Snackbar.LENGTH_INDEFINITE)
            } else {
                snackbar?.dismiss()
            }
        })

        model.errorMessage.observe(this@AboutActivity, Observer {
            it!!.getMessage(this@AboutActivity)?.let { showSnack(it) }
        })

        model.versionUpdateResult.observe(this@AboutActivity, Observer {
            showSnack(R.string.check_update_new_update_available,
                    R.string.btn_title_update,
                    View.OnClickListener {
                        //Open the play store.
                        SUUtils.openLink(this@AboutActivity, getString(R.string.rate_app_url))
                    },
                    Snackbar.LENGTH_INDEFINITE)
        })

        //Check for the update automatically
        model.checkForUpdate(this@AboutActivity)
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
                .setOnClickAction({ model.handleRateUs(this@AboutActivity) })
                .build()

        //Issue
        val reportIssueItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_bug_report_brown)
                .setIconGravity(Gravity.START)
                .text("Report an issue")
                .subText("Are you facing any issue? Report it here.")
                .setOnClickAction({ model.handleRateUs(this@AboutActivity) })
                .build()

        //Donate
        val donateItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_heart_fill_red)
                .setIconGravity(Gravity.START)
                .text("Support Development")
                .setOnClickAction({ model.handleSupportDevelopment(this@AboutActivity) })
                .build()

        //Share
        val shareItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_share_blue)
                .setIconGravity(Gravity.START)
                .text("Share with friends")
                .setOnClickAction({ model.handleShareWithFriends(this@AboutActivity) })
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
                .setOnClickAction({ model.checkForUpdate(this@AboutActivity) })
                .build()

        //open source libs
        val openSourceLibsItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_logo_opensource)
                .setIconGravity(Gravity.START)
                .text("Open source libraries")
                .setOnClickAction({ model.handleOpenSourceLibs(this@AboutActivity) })
                .build()

        //open source libs
        val visitOnGithubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_github_white)
                .setIconGravity(Gravity.START)
                .text("View on GitHub")
                .setOnClickAction({ model.handleOpenGitHubProject(this@AboutActivity) })
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
                .addItem(visitOnGithubItem)
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
                .setOnClickAction({ model.handleAuthorProfile(this@AboutActivity) })
                .build()

        //Github
        val githubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_github_white)
                .setIconGravity(Gravity.START)
                .text("Follow on GitHub")
                .subText("github.com/kevalpatel2106")
                .setOnClickAction({ model.handelFollowAuthorOnGitHub(this@AboutActivity) })
                .build()


        //Email
        val emailItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_email_white)
                .setIconGravity(Gravity.START)
                .text("Send an Email")
                .subText("kevalpatel2106@gmail.com")
                .setOnClickAction({ model.handleAuthorEmail(this@AboutActivity) })
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
                .setOnClickAction({ model.handleJoinSlack(this@AboutActivity) })
                .build()

        //Twitter
        val twitterItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_twitter)
                .setIconGravity(Gravity.START)
                .text("Follow on Twitter")
                .subText("@kevalpatel2106")
                .setOnClickAction({ model.handleFollowProjectTwitter(this@AboutActivity) })
                .build()

        //Fork on GitHub
        val githubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_fork)
                .setIconGravity(Gravity.START)
                .text("Fork on GitHub")
                .setOnClickAction({ model.handleForkOnGitHub(this@AboutActivity) })
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
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == model.REQUEST_CODE_INVITE) {
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
