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

package com.kevalpatel2106.standup.dashboard


import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.base.uiController.showSnack
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.timelineview.TimeLineLength
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.layout_home_efficiency_card.*
import kotlinx.android.synthetic.main.layout_home_timeline_card.*


/**
 * A simple [Fragment] subclass.
 */
class DashboardFragment : Fragment() {

    companion object {

        /**
         * Get the new instance of [DashboardFragment].
         */
        fun getNewInstance(): DashboardFragment {
            return DashboardFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        today_time_line.timelineDuration = TimeLineLength.A_DAY

        setModel()
    }

    private fun setModel() {
        val model = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        model.setPieChart(context!!, home_efficiency_card_pie_chart)
        model.setPieChartData(context!!, home_efficiency_card_pie_chart, 0F, 0F)

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

                model.setPieChartData(context = context!!,
                        pieChart = home_efficiency_card_pie_chart,
                        sittingDurationPercent = it.sittingPercent,
                        standingDurationPercent = it.standingPercent)

                tracked_time_tv.text = it.durationTimeHours
                total_standing_time_tv.text = it.standingTimeHours
                total_sitting_time_tv.text = it.sittingTimeHours
            }
        })

        //Observe events for time line card
        model.timelineEventsList.observe(this@DashboardFragment, Observer {
            it?.let {
                time_line_card.visibility = View.VISIBLE
                today_time_line.timelineItems = it
            }
        })

        //Load the today's summary
        model.getTodaysSummary()
    }
}
