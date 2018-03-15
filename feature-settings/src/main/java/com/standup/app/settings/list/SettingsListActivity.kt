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

@file:Suppress("DEPRECATION")

package com.standup.app.settings.list

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.danielstone.materialaboutlibrary.adapters.MaterialAboutListAdapter
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.utils.alert
import com.standup.app.settings.R
import com.standup.app.settings.SettingsHook
import com.standup.app.settings.di.DaggerSettingsComponent
import com.standup.app.settings.instructions.InstructionActivity
import com.standup.app.settings.whitelisting.WhitelistDialog
import dagger.Lazy
import kotlinx.android.synthetic.main.activity_settings_list.*
import javax.inject.Inject

class SettingsListActivity : BaseActivity() {

    private lateinit var model: SettingsViewModel

    private var isTwoPane = false

    @Inject
    internal lateinit var adapter: MaterialAboutListAdapter

    @Inject
    lateinit var settingsHook: Lazy<SettingsHook>

    private var refreshList: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SettingsListActivity)

        setContentView(R.layout.activity_settings_list)

        //Set the actionbar
        setToolbar(R.id.include2, R.string.title_activity_settings, true)

        //This indicates that settings is running on the two pane mode in the tablet mode.
        isTwoPane = settings_detail_frame_layout != null

        //Set the recycler view
        settings_list_rv.layoutManager = LinearLayoutManager(this@SettingsListActivity)
        settings_list_rv.itemAnimator = DefaultItemAnimator()
        settings_list_rv.adapter = adapter

        setViewModel()

        if (savedInstanceState == null) {        //For the first time activity created...

            //Select the first item by default
            if (isTwoPane) {
                model.openSyncSettings(
                        context = this@SettingsListActivity,
                        isTwoPane = true
                )
            }

            model.prepareSettingsList(this@SettingsListActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        if (refreshList) model.prepareSettingsList(this@SettingsListActivity)
    }

    private fun setViewModel() {
        model = ViewModelProviders.of(this@SettingsListActivity).get(SettingsViewModel::class.java)

        //Detail fragment to load for tablets
        model.detailFragment.observe(this@SettingsListActivity, Observer {
            it.let {
                if (!isTwoPane) return@let

                supportFragmentManager.beginTransaction()
                        .replace(R.id.settings_detail_frame_layout, it)
                        .commit()
            }
        })

        model.settingsItems.observe(this@SettingsListActivity, Observer {
            it?.let {
                adapter.setData(it)
                adapter.notifyDataSetChanged()
            }
        })

        model.showLogoutConformation.observe(this, Observer {
            it?.let {
                if (it) {
                    showLogoutConfirmation()
                }
            }
        })

        model.openSyncSettings.observe(this, Observer {
            it?.let {
                if (it) {
                    model.openSyncSettings(this@SettingsListActivity, isTwoPane)
                }
            }
        })

        model.openNotificationSettings.observe(this, Observer {
            it?.let {
                if (it) {
                    model.openNotificationSettings(this@SettingsListActivity, isTwoPane)
                }
            }
        })

        model.openDailyReview.observe(this, Observer {
            it?.let {
                if (it) {
                    model.openDailyReviewSettings(this@SettingsListActivity, isTwoPane)
                }
            }
        })

        model.openDndSettings.observe(this, Observer {
            it?.let {
                if (it) {
                    model.openDNDSettings(this@SettingsListActivity, isTwoPane)
                }
            }
        })

        model.showWhitelistDialog.observe(this, Observer {
            it?.let {
                if (it) {
                    refreshList = true
                    WhitelistDialog.showDialog(this@SettingsListActivity, supportFragmentManager)
                }
            }
        })

        model.openPrivacyPolicy.observe(this, Observer {
            it?.let {
                if (it) {
                    /* TODO Open privacy policy */
                }
            }
        })

        model.openInstructions.observe(this, Observer {
            it?.let {
                if (it) {
                    InstructionActivity.launch(this@SettingsListActivity)
                }
            }
        })
    }

    private fun showLogoutConfirmation() {
        alert(
                messageResource = R.string.sign_out_warning_message,
                titleResource = R.string.sign_out_warning_title,
                func = {
                    positiveButton(R.string.sign_out_warning_positive_btn_title, {

                        //Display progress dialog
                        ProgressDialog(this@SettingsListActivity).apply {
                            isIndeterminate = true
                            setMessage("Logging out...")
                            setCancelable(false)
                        }.show()

                        settingsHook.get().logout()
                    })
                    negativeButton(android.R.string.cancel, { /* NO OP */ })
                }
        )
    }

    companion object {

        /**
         * Launch the [SettingsListActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        internal fun launch(context: Context) {
            val launchIntent = Intent(context, SettingsListActivity::class.java)
            context.startActivity(launchIntent)
        }
    }
}
