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

package com.standup.app.diary.detail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.CardView
import android.view.animation.AnimationUtils
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.common.misc.TimeLineFullActivity
import com.kevalpatel2106.common.misc.setPieChart
import com.kevalpatel2106.common.misc.setPieChartData
import com.kevalpatel2106.common.misc.timeline.setUserActivities
import com.kevalpatel2106.common.purchase.PurchaseActivity
import com.kevalpatel2106.common.view.BaseTextView
import com.kevalpatel2106.utils.TimeUtils
import com.standup.app.diary.R
import com.standup.app.diary.userActivityList.UserActivityListActivity
import com.standup.timelineview.TimeLineLength
import com.standup.timelineview.TimeLineView
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.layout_open_user_activity_card.*

class DetailActivity : BaseActivity() {

    /**
     * @see DetailViewModel
     */
    private lateinit var model: DetailViewModel

    private var dayOfMonth: Int = 0
    private var month: Int = 0
    private var year: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseArguments()

        setContentView(R.layout.activity_detail)
        setToolbar(R.id.toolbar, "$dayOfMonth ${TimeUtils.getMonthInitials(month)} $year", true)

        //Set the timeline
        findViewById<TimeLineView>(R.id.today_time_line).timelineDuration = TimeLineLength.A_DAY
        findViewById<TimeLineView>(R.id.today_time_line).enableTouch = false

        //Set the pie chart
        detail_efficiency_card_pie_chart.setPieChart(this@DetailActivity,
                resources.getInteger(R.integer.pie_chart_animation_duration))
        detail_efficiency_card_pie_chart.setPieChartData(this@DetailActivity, 0F, 0F)

        setViewModel()

        if (savedInstanceState == null) {
            //Play those animations
            findViewById<CardView>(R.id.efficiency_card).startAnimation(AnimationUtils
                    .loadAnimation(this@DetailActivity, R.anim.slide_in_bottom))
            findViewById<CardView>(R.id.time_line_card).startAnimation(AnimationUtils
                    .loadAnimation(this@DetailActivity, R.anim.slide_in_bottom))
            view_user_activity_card.startAnimation(AnimationUtils
                    .loadAnimation(this@DetailActivity, R.anim.slide_in_bottom))

            model.checkIsPremiumUser()
        }

        view_user_activity_card.setOnClickListener {
            UserActivityListActivity.launch(this@DetailActivity, dayOfMonth, month, year)
        }

        findViewById<CardView>(R.id.time_line_card).setOnClickListener {
            model.summary.value?.let {
                TimeLineFullActivity.launch(this@DetailActivity, it)
            }
        }
    }

    /**
     * Set up the [DetailViewModel].
     *
     * @see DetailViewModel
     */
    private fun setViewModel() {
        model = ViewModelProviders.of(this@DetailActivity).get(DetailViewModel::class.java)
        model.blockUi.observe(this@DetailActivity, Observer {
            it?.let {
                if (it) {
                    detail_view_flipper.displayedChild = 1  /* Display loader */
                }
            }
        })
        model.errorMessage.observe(this@DetailActivity, Observer {
            it?.let {
                detail_error_view.setError(it)
                detail_view_flipper.displayedChild = 2  /* Display error */
            }
        })
        model.summary.observe(this@DetailActivity, Observer {
            it?.let {
                //Display summary
                detail_view_flipper.displayedChild = 0

                detail_efficiency_card_pie_chart.setPieChartData(context = this@DetailActivity,
                        sittingDurationPercent = it.sittingPercent,
                        standingDurationPercent = it.standingPercent)

                findViewById<BaseTextView>(R.id.tracked_time_tv).text = it.trackedDurationHours
                findViewById<BaseTextView>(R.id.total_standing_time_tv).text = it.standingTimeHours
                findViewById<BaseTextView>(R.id.total_sitting_time_tv).text = it.sittingTimeHours

                findViewById<TimeLineView>(R.id.today_time_line).setUserActivities(it.dayActivity)
            }
        })
        model.isPremiumUser.observe(this@DetailActivity, Observer {
            it?.let {
                if (!it) {  //Not a pro user
                    PurchaseActivity.launch(this@DetailActivity)
                    finish()
                } else {    //Pro user
                    //Load the summary from the database
                    model.fetchData(dayOfMonth, month, year)
                }
            }
        })
    }

    /**
     * Parse the arguments provided into the launch intent. This will parse [dayOfMonth], [month] and
     * [year] from the launch intent.
     *
     * @see launch
     */
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
        /**
         * Key for the [dayOfMonth] argument. The value for this key must be an integer and the
         * value must be in [1,31].
         */
        private const val ARG_DAY_OF_MONTH = "arg_day_of_month"

        /**
         * Key for the [month] argument. This should contain integer and the value must be in [0,11].
         */
        private const val ARG_MONTH = "arg_month"

        /**
         * Key for the [year] argument. The value for the key must be integer and it should be in
         * [1900,2100].
         */
        private const val ARG_YEAR = "arg_year"

        /**
         * Launch the [DetailActivity] with the [context]. This [DetailActivity] will display
         * detailed summary for [dayOfMonth]-[month]-[year].
         *
         * - [dayOfMonth] must be in [1,31].
         * - [month] must be in [0,11].
         * - [year] must be in [1900, 2100].
         *
         * @see DetailActivity
         */
        internal fun launch(context: Context,
                            dayOfMonth: Int,
                            monthOfYear: Int,
                            year: Int) {
            context.startActivity(launchIntent(context, dayOfMonth, monthOfYear, year))
        }

        /**
         * Launch the [DetailActivity] with the [context]. This [DetailActivity] will display
         * detailed summary for [dayOfMonth]-[month]-[year].
         *
         * - [dayOfMonth] must be in [1,31].
         * - [month] must be in [0,11].
         * - [year] must be in [1900, 2100].
         *
         * @see DetailActivity
         */
        internal fun launchIntent(context: Context,
                                  dayOfMonth: Int,
                                  monthOfYear: Int,
                                  year: Int): Intent {

            return Intent(context, DetailActivity::class.java).apply {
                putExtra(ARG_DAY_OF_MONTH, dayOfMonth)
                putExtra(ARG_MONTH, monthOfYear)
                putExtra(ARG_YEAR, year)
            }
        }
    }
}
