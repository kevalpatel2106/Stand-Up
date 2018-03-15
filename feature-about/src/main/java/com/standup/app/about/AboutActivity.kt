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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.google.android.gms.appinvite.AppInviteInvitation
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.misc.AnalyticsEvents
import com.kevalpatel2106.common.misc.logEvent
import com.kevalpatel2106.utils.alert
import com.standup.app.about.repo.CheckVersionResponse

class AboutActivity : MaterialAboutActivity() {

    private lateinit var model: AboutViewModel
    private var versionItem: MaterialAboutActionItem? = null

    companion object {

        /**
         * Launch the [AboutActivity] activity.
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        internal fun launch(context: Context) {
            val launchIntent = Intent(context, AboutActivity::class.java)
            context.startActivity(launchIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        model = ViewModelProviders.of(this@AboutActivity).get(AboutViewModel::class.java)

        super.onCreate(savedInstanceState)

        model.isCheckingUpdate.observe(this@AboutActivity, Observer {
            if (it!!) {
                versionItem?.subTextRes = (R.string.about_check_for_update)
            } else {
                versionItem?.subText = BuildConfig.VERSION_NAME
            }
            refreshMaterialAboutList()
        })

        model.errorMessage.observe(this@AboutActivity, Observer {
            it!!.getMessage(this@AboutActivity)?.let { showSnack(it) }
        })

        model.versionUpdateResult.observe(this@AboutActivity, Observer {
            //Refresh the list to display the update card
            refreshMaterialAboutList()
        })

        if (savedInstanceState == null) {
            //Check for the update automatically
            model.checkForUpdate()
        }
    }

    override fun getActivityTitle(): CharSequence = getString(R.string.application_name)

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        val aboutList = MaterialAboutList()
        aboutList.addCard(getAboutCard())
        model.versionUpdateResult.value?.let {
            //Add update card if the new version is available
            aboutList.addCard(getUpdateCard(model.versionUpdateResult.value!!))
        }
        aboutList.addCard(getSupportUsCard())
        aboutList.addCard(getJoinUsCard())
        aboutList.addCard(getAuthorCard())
        return aboutList
    }


    private fun getUpdateCard(update: CheckVersionResponse): MaterialAboutCard {
        return MaterialAboutCard.Builder()
                .addItem(MaterialAboutActionItem.Builder()
                        .icon(R.drawable.ic_update)
                        .setIconGravity(Gravity.START)
                        .text(R.string.check_update_new_update_available)
                        .subText("New version is " + update.latestVersionName + ". Click here to update.")
                        .setOnLongClickAction {
                            update.releaseNotes?.let {
                                //Show the release note
                                alert(message = update.releaseNotes, title = getString(R.string.alert_title_whats_new)).show()
                            }
                        }
                        .setOnClickAction({
                            //Open the play store.
                            AboutUtils.openLink(this@AboutActivity, getString(R.string.rate_app_url))
                        })
                        .build())
                .build()
    }

    private fun getSupportUsCard(): MaterialAboutCard {
        //Rate
        val rateUsItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_star_orange)
                .setIconGravity(Gravity.START)
                .text(R.string.about_rate_this_app)
                .setOnClickAction({
                    logEvent(AnalyticsEvents.EVENT_RATE_APP_ON_PLAY_STORE)
                    model.handleRateUs(this@AboutActivity)
                })
                .build()

        //Issue
        val reportIssueItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_bug_report_brown)
                .setIconGravity(Gravity.START)
                .text(R.string.about_report_issue_title)
                .subText(R.string.about_report_issue_subtitle)
                .setOnClickAction({ model.handleReportIssue(this@AboutActivity) })
                .build()

        //Donate
        val donateItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_heart_fill_red)
                .setIconGravity(Gravity.START)
                .text(R.string.about_support_development_title)
                .setOnClickAction({ model.handleSupportDevelopment(this@AboutActivity) })
                .build()

        //Share
        val shareItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_share_blue)
                .setIconGravity(Gravity.START)
                .text(R.string.about_share_with_friends_title)
                .setOnClickAction({ model.handleShareWithFriends(this@AboutActivity) })
                .build()

        return MaterialAboutCard.Builder()
                .title(R.string.about_support_us_card_title)
                .addItem(rateUsItem)
                .addItem(reportIssueItem)
                .addItem(donateItem)
                .addItem(shareItem)
                .build()
    }


    private fun getAboutCard(): MaterialAboutCard {
        //CheckVersionResponse
        versionItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_version_white)
                .setIconGravity(Gravity.START)
                .text(R.string.about_check_version_title)
                .subText(BuildConfig.VERSION_NAME)
                .setOnClickAction({
                    logEvent(AnalyticsEvents.EVENT_CHECK_UPDATE_MANUALLY)
                    model.checkForUpdate()
                })
                .build()

        //open source libs
        val openSourceLibsItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_logo_opensource)
                .setIconGravity(Gravity.START)
                .text(R.string.about_open_source_libs_title)
                .setOnClickAction({
                    model.handleOpenSourceLibs(this@AboutActivity)
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
                .title(R.string.about_card_title_about)
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
                .text(R.string.about_author_name_title)
                .subText(R.string.about_author_name_subtitle)
                .setOnClickAction({
                    model.handleAuthorProfile(this@AboutActivity)
                })
                .build()

        //Github
        val githubItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_github_white)
                .setIconGravity(Gravity.START)
                .text(R.string.about_author_github_title)
                .subText(R.string.about_author_github_subtitle)
                .setOnClickAction({
                    model.handelFollowAuthorOnGitHub(this@AboutActivity)
                })
                .build()


        //Twitter
        val twitterItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_twitter)
                .setIconGravity(Gravity.START)
                .text(R.string.about_author_twitter_title)
                .subText(R.string.about_author_twitter_subtitle)
                .setOnClickAction({ model.handleFollowAuthorTwitter(this@AboutActivity) })
                .build()


        return MaterialAboutCard.Builder()
                .title(R.string.about_card_title_author)
                .addItem(authName)
                .addItem(githubItem)
                .addItem(twitterItem)
                .build()
    }

    private fun getJoinUsCard(): MaterialAboutCard {

        //Email
        val emailItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_email_white)
                .setIconGravity(Gravity.START)
                .text(R.string.about_app_send_email_title)
                .subText(R.string.support_email)
                .setOnClickAction({ model.handleSupportEmail(this@AboutActivity) })
                .build()


        //Slack
        val slackItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_slack)
                .setIconGravity(Gravity.START)
                .text(R.string.about_author_join_slack_title)
                .subText(R.string.slack_channel_url)
                .setOnClickAction({
                    logEvent(AnalyticsEvents.EVENT_JOIN_SLACK_CHANNEL)
                    model.handleJoinSlack(this@AboutActivity)
                })
                .build()

        //Twitter
        val twitterItem = MaterialAboutActionItem.Builder()
                .icon(R.drawable.ic_twitter)
                .setIconGravity(Gravity.START)
                .text(R.string.about_app_twitter_title)
                .subText(R.string.about_app_twitter_subtitle)
                .setOnClickAction({ model.handleFollowProjectTwitter(this@AboutActivity) })
                .build()

        return MaterialAboutCard.Builder()
                .title(R.string.about_app_connect_with_us_title)
                .addItem(emailItem)
                .addItem(twitterItem)
                .addItem(slackItem)
                .build()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == model.REQUEST_CODE_INVITE) {
            if (resultCode == RESULT_OK && data != null) {
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
