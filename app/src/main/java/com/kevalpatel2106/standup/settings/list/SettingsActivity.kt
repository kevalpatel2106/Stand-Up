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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.kevalpatel2106.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : BaseActivity(), SettingsClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setToolbar(R.id.include2, R.string.title_activity_settings, true)

        settings_list_rv.layoutManager = LinearLayoutManager(this@SettingsActivity)
        settings_list_rv.itemAnimator = DefaultItemAnimator()

        val model = ViewModelProviders.of(this@SettingsActivity).get(SettingsViewModel::class.java)
        model.settingsItemList.observe(this@SettingsActivity, Observer {
            it?.let {
                val adapter = SettingsListAdapter(this@SettingsActivity, it, this@SettingsActivity)
                settings_list_rv.adapter = adapter
            }
        })

        model.prepareSettingsList()
    }

    override fun onItemClick(clickedItem: SettingsItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {

        /**
         * Launch the [SettingsActivity].
         *
         * @param context Instance of the caller.
         */
        @JvmStatic
        fun launch(context: Context) {
            val launchIntent = Intent(context, SettingsActivity::class.java)
            context.startActivity(launchIntent)
        }
    }
}
