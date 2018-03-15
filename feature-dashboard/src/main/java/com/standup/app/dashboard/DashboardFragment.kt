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

package com.standup.app.dashboard


import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.kevalpatel2106.common.base.uiController.BaseFragment
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.misc.TimeLineFullActivity
import com.kevalpatel2106.common.misc.setPieChart
import com.kevalpatel2106.common.misc.setPieChartData
import com.kevalpatel2106.common.misc.timeline.setUserActivities
import com.kevalpatel2106.common.view.BaseTextView
import com.standup.timelineview.TimeLineView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.layout_next_notification_card.*

/**
 * A simple [Fragment] subclass tha will display the current summary and notify user about important
 * events. This fragment is the "Home" screen for the application.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class DashboardFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    private lateinit var today_time_line: TimeLineView
    private lateinit var efficiency_card: CardView
    private lateinit var time_line_card: CardView
    private lateinit var tracked_time_tv: BaseTextView
    private lateinit var total_standing_time_tv: BaseTextView
    private lateinit var total_sitting_time_tv: BaseTextView

    /**
     * @see DashboardViewModel
     */
    private lateinit var model: DashboardViewModel

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize views
        today_time_line = view.findViewById(R.id.today_time_line)
        efficiency_card = view.findViewById(R.id.efficiency_card)
        time_line_card = view.findViewById(R.id.time_line_card)
        tracked_time_tv = view.findViewById(R.id.tracked_time_tv)
        total_standing_time_tv = view.findViewById(R.id.total_standing_time_tv)
        total_sitting_time_tv = view.findViewById(R.id.total_sitting_time_tv)

        //Set time line view.
        today_time_line.enableTouch = false

        //Set pie chart
        home_efficiency_card_pie_chart.setPieChart(context!!,
                context!!.resources.getInteger(R.integer.pie_chart_animation_duration))
        home_efficiency_card_pie_chart.setPieChartData(context!!, 0F, 0F)

        setModel()

        if (savedInstanceState == null) {
            //Play those animations
            efficiency_card.startAnimation(AnimationUtils
                    .loadAnimation(context, R.anim.slide_in_bottom))
            time_line_card.startAnimation(AnimationUtils
                    .loadAnimation(context, R.anim.slide_in_bottom))
            time_line_card.startAnimation(AnimationUtils
                    .loadAnimation(context, R.anim.slide_in_bottom))
            next_reminder_card.startAnimation(AnimationUtils
                    .loadAnimation(context, R.anim.slide_in_bottom))
        }

        time_line_card.setOnClickListener {
            model.todaySummary.value?.let {
                TimeLineFullActivity.launch(context!!, it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //Load the today's summary
        //Whenever activity comes into the foreground we need fresh data
        model.getTodaysSummary()
        model.loadNextNotificationTime()
    }

    /**
     * Set up the [DashboardViewModel] and start observing the properties from [DashboardViewModel].
     * This will update [home_efficiency_card_pie_chart] and [today_time_line] based on the updated
     * summary.
     *
     * @see DashboardViewModel
     */
    private fun setModel() {
        model = ViewModelProviders.of(this).get(DashboardViewModel::class.java)

        //Observe error messages
        model.errorMessage.observe(this@DashboardFragment, Observer {
            it!!.getMessage(context)?.let { showSnack(it) }
        })

        //Observe events for efficiency card
        model.todaySummaryStartLoading.observe(this@DashboardFragment, Observer {
            efficiency_card_view_flipper.displayedChild = 1
        })
        model.todaySummaryErrorCallback.observe(this@DashboardFragment, Observer {
            it?.let {
                efficiency_card_view_flipper.displayedChild = 2

                //Display error view
                efficiency_card_error_view.setError(it)
                time_line_card.visibility = View.GONE
            }
        })
        model.todaySummary.observe(this@DashboardFragment, Observer {
            it?.let {
                //Display summary
                efficiency_card_view_flipper.displayedChild = 0

                home_efficiency_card_pie_chart.setPieChartData(context = context!!,
                        sittingDurationPercent = it.sittingPercent,
                        standingDurationPercent = it.standingPercent)

                tracked_time_tv.text = it.trackedDurationHours
                total_standing_time_tv.text = it.standingTimeHours
                total_sitting_time_tv.text = it.sittingTimeHours

                today_time_line.setUserActivities(it.dayActivity)
            }
        })
        model.nextReminderStatus.observe(this@DashboardFragment, Observer {
            it?.let {
                next_notification_tv.text = it
            }
        })
    }

    companion object {

        /**
         * Get the new instance of [DashboardFragment].
         */
        internal fun getNewInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }
}
