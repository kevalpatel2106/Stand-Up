package com.kevalpatel2106.standup.dashboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.timelineview.TimeLineItem
import com.kevalpatel2106.standup.timelineview.TimeLineLength
import kotlinx.android.synthetic.main.layout_home_timeline_card.*


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    companion object {

        /**
         * Get the new instance of [HomeFragment].
         */
        fun getNewInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Timeline view
        today_time_line.timelineDuration = TimeLineLength.A_DAY

        val timelineItems = ArrayList<TimeLineItem>()
        timelineItems.add(TimeLineItem(
                startTime = 3600,
                endTime = 2 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = (2.5 * 3600).toLong(),
                endTime = 3 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 3 * 3600,
                endTime = (3.25 * 3600).toLong(),
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 6 * 3600,
                endTime = 8 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 5 * 3600,
                endTime = (5.90 * 3600).toLong(),
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_green_dark)
        ))
        timelineItems.add(TimeLineItem(
                startTime = 12 * 3600,
                endTime = 23 * 3600,
                color = ContextCompat.getColor(this.context!!, android.R.color.holo_orange_dark)
        ))
        today_time_line.timelineItems = timelineItems
    }
}
