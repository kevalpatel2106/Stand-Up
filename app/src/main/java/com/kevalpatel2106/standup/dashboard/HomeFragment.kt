package com.kevalpatel2106.standup.dashboard


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kevalpatel2106.standup.R


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

}
