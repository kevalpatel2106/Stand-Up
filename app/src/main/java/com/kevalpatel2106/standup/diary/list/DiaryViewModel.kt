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

package com.kevalpatel2106.standup.diary.list

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.CallbackEvent
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.diary.repo.DiaryRepo
import com.kevalpatel2106.standup.diary.repo.DiaryRepoImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Keval on 23/12/17.
 * View model for [com.kevalpatel2106.standup.diary.list.DiaryFragment].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(DiaryFragment::class)
internal class DiaryViewModel : BaseViewModel {

    /**
     * List of user activities.
     */
    val activities = MutableLiveData<ArrayList<DailyActivitySummary>>()

    val noMoreData = MutableLiveData<Boolean>()

    val pageLoadingCompleteCallback = CallbackEvent()

    @VisibleForTesting
    internal val mUserActivityRepo: DiaryRepo

    /**
     * Private constructor to add the custom [DiaryRepo] for testing.
     *
     * @param diaryRepo Add your own [DiaryRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(diaryRepo: DiaryRepo) : super() {
        this.mUserActivityRepo = diaryRepo

        //Load the first page
        loadNext(System.currentTimeMillis(), true)
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mUserActivityRepo = DiaryRepoImpl()

        //Load the first page
        loadNext(System.currentTimeMillis(), true)
    }

    init {
        noMoreData.value = false
        activities.value = ArrayList()
    }

    internal fun loadNext(oldestEventTimeMills: Long,
                          isFirstPage: Boolean = false) {
        addDisposable(mUserActivityRepo
                .loadDaysSummaryList(oldestEventTimeMills - 1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe({
                    noMoreData.value = false
                    if (isFirstPage) blockUi.value = true
                })
                .doOnNext {
                    //Do not block UI.
                    blockUi.value = false
                }
                .doOnComplete {
                    blockUi.value = false

                    activities.value?.let {

                        //If there are no items on the list...
                        //Display no data found view
                        if (it.isEmpty()) {
                            val message = ErrorMessage("No data available!!")
                            errorMessage.value = message
                            noMoreData.value = true
                        } else if (it.size.rem(DiaryRepo.PAGE_SIZE) != 0) {
                            //If after terminate there not enough item that can fill the page...
                            //Indicates there is no more data to show.
                            noMoreData.value = true
                        }
                    }
                }
                .doOnTerminate {
                    pageLoadingCompleteCallback.dispatch()
                }
                .subscribe({
                    //Update data
                    @Suppress("UnnecessaryVariable")
                    val newItem = it
                    activities.value?.let {

                        if (it.isEmpty()) { //It's the first item
                            //Add the month header.
                            it.add(MonthHeader(newItem.monthOfYear, newItem.year))
                        } else if (newItem !is MonthHeader && it.last() !is MonthHeader) {

                            //Last item is view,
                            if (newItem.monthOfYear != it.last().monthOfYear     //Month is changed
                                    || newItem.year != it.last().year) {        //Year is changed
                                //Add the month header.
                                it.add(MonthHeader(newItem.monthOfYear, newItem.year))
                            }
                        }


                        //Add the new item
                        it.add(newItem)
                    }
                    activities.value = activities.value

                }, {
                    blockUi.value = false

                    val message = ErrorMessage(it.message)
                    message.setErrorBtn(R.string.btn_title_retry, {

                        //Call this function again
                        loadNext(oldestEventTimeMills, isFirstPage)
                    })
                    errorMessage.value = message
                }))
    }

}