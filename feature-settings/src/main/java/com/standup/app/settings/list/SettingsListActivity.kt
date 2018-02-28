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
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.utils.alert
import com.standup.app.settings.R
import com.standup.app.settings.SettingsHook
import com.standup.app.settings.di.DaggerSettingsComponent
import dagger.Lazy
import kotlinx.android.synthetic.main.activity_settings_list.*
import javax.inject.Inject

class SettingsListActivity : BaseActivity(), SettingsClickListener {

    private lateinit var model: SettingsViewModel

    private var isTwoPane = false

    @Inject
    lateinit var settingsHook: Lazy<SettingsHook>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_list)

        DaggerSettingsComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@SettingsListActivity)

        //Set the actionbar
        setToolbar(R.id.include2, R.string.title_activity_settings, true)

        //This indicates that settings is running on the two pane mode in the tablet mode.
        isTwoPane = settings_detail_frame_layout != null

        setViewModel()

        //Set the recycler view
        settings_list_rv.layoutManager = LinearLayoutManager(this@SettingsListActivity)
        settings_list_rv.itemAnimator = DefaultItemAnimator()
        settings_list_rv.adapter = SettingsListAdapter(this@SettingsListActivity,
                model.settingsItems.value!!, this@SettingsListActivity)
        model.settingsItems.observe(this@SettingsListActivity, Observer {
            it?.let { settings_list_rv.adapter.notifyDataSetChanged() }
        })

        //Select the first item by default
        if (isTwoPane) onItemClick(model.settingsItems.value!![0])
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

        model.showLogoutConformation.observe(this, Observer {
            it?.let {
                if (it) {
                    showLogoutConfirmation()
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

    override fun onItemClick(clickedItem: SettingsItem) {
        model.onSettingsClicked(this@SettingsListActivity, clickedItem, isTwoPane)
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
