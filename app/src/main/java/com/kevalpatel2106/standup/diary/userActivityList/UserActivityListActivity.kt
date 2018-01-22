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

package com.kevalpatel2106.standup.diary.userActivityList

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R
import kotlinx.android.synthetic.main.activity_user_activity_list.*

/**
 * Created by Kevalpatel2106 on 22-Jan-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class UserActivityListActivity : BaseActivity() {

    private lateinit var model: UserActivityListModel

    private var dayOfMonth: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArguments()

        setContentView(R.layout.activity_user_activity_list)

        setToolbar(R.id.include, getString(R.string.title_activity_user_activity_list), true)

        model = ViewModelProviders.of(this@UserActivityListActivity).get(UserActivityListModel::class.java)

        //Set recycler view
        user_activity_list_rv.layoutManager = LinearLayoutManager(this@UserActivityListActivity)
        user_activity_list_rv.itemAnimator = DefaultItemAnimator()
        user_activity_list_rv.adapter = UserActivityAdapter(this@UserActivityListActivity, model.userActivities.value!!)
        model.userActivities.observe(this@UserActivityListActivity, Observer {
            it?.let {
                user_activity_flipper.displayedChild = 0  /* Display list */
                user_activity_list_rv.adapter.notifyDataSetChanged()
            }
        })
        model.blockUi.observe(this@UserActivityListActivity, Observer {
            it?.let {
                if (it) {
                    user_activity_flipper.displayedChild = 1  /* Display loader */
                }
            }
        })
        model.errorMessage.observe(this@UserActivityListActivity, Observer {
            it?.let {
                detail_error_view.setError(it)
                user_activity_flipper.displayedChild = 2  /* Display error */
            }
        })

        if (savedInstanceState == null) {
            model.fetchData(dayOfMonth, month, year)
        }
    }

    private fun parseArguments() {
        //Parse the arguments
        if (!intent.hasExtra(ARG_DAY_OF_MONTH) || !intent.hasExtra(ARG_MONTH)
                || !intent.hasExtra(ARG_YEAR)) {
            throw IllegalStateException("Provide proper arguments with day of month, month and year.")
        }

        //Get the date
        dayOfMonth = intent.getIntExtra(ARG_DAY_OF_MONTH, -1)
        if (!Validator.isValidDate(dayOfMonth)) {
            throw IllegalArgumentException("Invalid day of month : $dayOfMonth.")
        }

        //Get the month
        month = intent.getIntExtra(ARG_MONTH, -1)
        if (!Validator.isValidMonth(month)) {
            throw IllegalArgumentException("Invalid month : $month.")
        }

        //Get the year
        year = intent.getIntExtra(ARG_YEAR, -1)
        if (!Validator.isValidYear(year)) {
            throw IllegalArgumentException("Invalid year : $year.")
        }
    }

    companion object {
        private const val ARG_DAY_OF_MONTH = "arg_day_of_month"
        private const val ARG_MONTH = "arg_month"
        private const val ARG_YEAR = "arg_year"

        fun launch(context: Context,
                   dayOfMonth: Int,
                   monthOfYear: Int,
                   year: Int) {

            context.startActivity(Intent(context, UserActivityListActivity::class.java).apply {
                putExtra(ARG_DAY_OF_MONTH, dayOfMonth)
                putExtra(ARG_MONTH, monthOfYear)
                putExtra(ARG_YEAR, year)
            })
        }
    }
}
