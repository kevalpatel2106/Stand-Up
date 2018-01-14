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

package com.kevalpatel2106.standup.settings.list

import android.app.ProgressDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import kotlinx.android.synthetic.main.activity_settings_list.*

class SettingsListActivity : BaseActivity(), SettingsClickListener {

    private lateinit var model: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_list)

        //Set the actionbar
        setToolbar(R.id.include2, R.string.title_activity_settings, true)

        model = ViewModelProviders.of(this@SettingsListActivity).get(SettingsViewModel::class.java)

        //This indicates that settings is running on the two pane mode in the tablet mode.
        model.isTwoPane = settings_detail_frame_layout != null

        //Detail fragment to load for tablets
        model.detailFragment.observe(this@SettingsListActivity, Observer {
            it.let {
                if (!model.isTwoPane) return@let

                supportFragmentManager.beginTransaction()
                        .add(R.id.settings_detail_frame_layout, it)
                        .commit()
            }
        })

        model.logoutInProgress.observe(this, Observer {
            it?.let {
                if (it) {
                    ProgressDialog(this@SettingsListActivity).apply {
                        isIndeterminate = true
                        setMessage("Logging out...")
                        setCancelable(false)
                    }.show()
                }
            }
        })

        //Set the recycler view
        settings_list_rv.layoutManager = LinearLayoutManager(this@SettingsListActivity)
        settings_list_rv.itemAnimator = DefaultItemAnimator()
        settings_list_rv.adapter = SettingsListAdapter(this@SettingsListActivity,
                model.settingsItems.value!!, this@SettingsListActivity)
        model.settingsItems.observe(this@SettingsListActivity, Observer {
            it?.let { settings_list_rv.adapter.notifyDataSetChanged() }
        })

        //Select the first item by default
        if (model.isTwoPane) onItemClick(model.settingsItems.value!![0])
    }

    override fun onItemClick(clickedItem: SettingsItem) {
        model.onSettingsClicked(this@SettingsListActivity, clickedItem)
    }

    companion object {

        /**
         * Launch the [SettingsListActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, SettingsListActivity::class.java)
            context.startActivity(launchIntent)
        }
    }
}
