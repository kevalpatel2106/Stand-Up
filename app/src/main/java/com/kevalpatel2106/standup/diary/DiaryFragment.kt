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
import com.kevalpatel2106.base.paging.PageRecyclerViewAdapter
import com.kevalpatel2106.base.uiController.BaseFragment
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.userActivity.UserActivity
import com.kevalpatel2106.utils.showSnack
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
        val adapter = DiaryListAdapter(mContext, model.activities.value!!, this@DiaryFragment)
        sleep_diary_rv.layoutManager = LinearLayoutManager(mContext)
        sleep_diary_rv.itemAnimator = DefaultItemAnimator()
        sleep_diary_rv.adapter = adapter

        //Observe error messages
        model.errorMessage.observe(this@DiaryFragment, Observer {
            it?.let {
                if (adapter.itemCount == 0) {
                    //Display the error view
                    sleep_diary_view_flipper.displayedChild = 2
                    dairy_error_view.setError(it)
                } else {
                    @Suppress("UnnecessaryVariable")
                    val errorMessage = it

                    //Display snackbar
                    showSnack(errorMessage.getMessage(context)!!,
                            getString(errorMessage.getErrorBtnText()),
                            View.OnClickListener { errorMessage.getOnErrorClick()?.invoke() })
                }
            }
        })

        model.blockUi.observe(this@DiaryFragment, Observer {
            it?.let {
                if (it) {
                    //Display the loader
                    sleep_diary_view_flipper.displayedChild = 1
                } else {
                    //Display the list
                    sleep_diary_view_flipper.displayedChild = 0
                }
            }
        })

        model.activities.observe(this@DiaryFragment, Observer {

            it?.let {
                if (it.isEmpty()) {
                    dairy_error_view.setError("No data available!!")
                    sleep_diary_view_flipper.displayedChild = 2
                } else {
                    sleep_diary_view_flipper.displayedChild = 0

                    //Refresh the list
                    adapter.notifyDataSetChanged()
                    adapter.onPageLoadComplete(true)
                }
            }
        })

        model.noMoreData.observe(this@DiaryFragment, Observer {
            it?.let { adapter.hasNextPage = !it }
        })
    }

    override fun onPageComplete(lastItem: UserActivity) {
        model.loadNext(lastItem.eventStartTimeMills)
    }

    override fun onItemSelected(pos: Int, item: UserActivity) {
        //TODO Open the detail page
    }
}
