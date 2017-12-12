package com.kevalpatel2106.standup.stats


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.standup.R


/**
 * A simple [Fragment] subclass.
 */
class StatsFragment : Fragment() {


    companion object {

        /**
         * Get the new instance of [StatsFragment].
         */
        fun getNewInstance(): StatsFragment {
            return StatsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

}