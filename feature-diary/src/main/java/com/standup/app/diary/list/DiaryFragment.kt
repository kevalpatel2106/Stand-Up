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

package com.standup.app.diary.list


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.common.base.paging.PageRecyclerViewAdapter
import com.kevalpatel2106.common.base.uiController.BaseFragment
import com.kevalpatel2106.common.base.uiController.showSnack
import com.kevalpatel2106.common.purchase.PurchaseActivity
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.standup.app.diary.R
import kotlinx.android.synthetic.main.fragment_diary.*


/**
 * A simple [Fragment] subclass.
 */
class DiaryFragment : BaseFragment(), PageRecyclerViewAdapter.RecyclerViewListener<DailyActivitySummary> {

    private lateinit var model: DiaryViewModel

    companion object {

        /**
         * Get the new instance of [DiaryFragment].
         */
        internal fun getNewInstance(): DiaryFragment {
            return DiaryFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_diary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        model = ViewModelProviders.of(this@DiaryFragment).get(DiaryViewModel::class.java)

        //Set the recycler view
        val adapter = DiaryListAdapter(mContext, model.activities.value!!, this@DiaryFragment)
        sleep_diary_rv.layoutManager = LinearLayoutManager(mContext)
        sleep_diary_rv.itemAnimator = DefaultItemAnimator()
        sleep_diary_rv.adapter = adapter

        //Observe when to display error view.
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

        //Observer when to display loader.
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

        //Observer when to display list with the refreshed data
        model.activities.observe(this@DiaryFragment, Observer {
            it?.let { adapter.notifyItemInserted(it.lastIndex) }
        })

        model.pageLoadingCompleteCallback.observe(this@DiaryFragment, Observer {
            adapter.onPageLoadComplete()
        })

        model.noMoreData.observe(this@DiaryFragment, Observer {
            it?.let { adapter.hasNextPage = !it }
        })

        model.moreItemsBlocked.observe(this@DiaryFragment, Observer {
            it?.let {
                if (it) {
                    showSnack(
                            message = R.string.diary_list_buy_now_message,
                            actionName = R.string.diary_list_buy_now_btn_title,
                            actionListener = View.OnClickListener {
                                context?.let { PurchaseActivity.launch(it) }
                            },
                            duration = Snackbar.LENGTH_INDEFINITE
                    )
                    adapter.hasNextPage = false
                }
            }
        })
    }

    private fun displayBuyPremium() {

    }

    override fun onPageComplete(lastItem: DailyActivitySummary) {
        model.loadDailySummaryPage(lastItem.dayActivity.first().eventStartTimeMills)
    }

    override fun onItemSelected(pos: Int, item: DailyActivitySummary) {
        // Do nothing
    }
}
