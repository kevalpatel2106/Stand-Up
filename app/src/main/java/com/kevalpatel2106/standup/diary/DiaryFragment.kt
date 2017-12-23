package com.kevalpatel2106.standup.diary


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.base.uiController.BaseFragment
import com.kevalpatel2106.base.uiController.PageRecyclerViewAdapter
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.userActivity.UserActivity
import kotlinx.android.synthetic.main.fragment_siting_diary.*


/**
 * A simple [Fragment] subclass.
 */
class DiaryFragment : BaseFragment(), PageRecyclerViewAdapter.RecyclerViewListener<UserActivity> {

    private lateinit var model: DiaryViewModel

    companion object {

        /**
         * Get the new instance of [DiaryFragment].
         */
        fun getNewInstance(): DiaryFragment {
            return DiaryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siting_diary, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProviders.of(this@DiaryFragment).get(DiaryViewModel::class.java)

        //Set the recycler view
        sleep_diary_rv.layoutManager = LinearLayoutManager(mContext)
        sleep_diary_rv.itemAnimator = DefaultItemAnimator()
        sleep_diary_rv.adapter = DiaryListAdapter(mContext, model.activities.value!!, this@DiaryFragment)
        model.activities.observe(this@DiaryFragment, Observer {
            //Refresh the list
            sleep_diary_rv.adapter.notifyDataSetChanged()
        })
    }

    override fun onPageComplete(nextPageCount: Int) {
        model.loadNext()
    }

    override fun onItemSelected(pos: Int, item: UserActivity) {

    }
}
