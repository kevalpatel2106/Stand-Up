/*
 *  Copyright 2017 Keval Patel.
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
import com.kevalpatel2106.standup.diary.repo.DairyRepo
import com.kevalpatel2106.standup.diary.repo.DairyRepoImpl
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
    internal val userActivityRepo: DairyRepo

    /**
     * Private constructor to add the custom [DairyRepo] for testing.
     *
     * @param dairyRepo Add your own [DairyRepo].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(dairyRepo: DairyRepo) : super() {
        this.userActivityRepo = dairyRepo

        //Load the first page
        loadNext(System.currentTimeMillis())
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        userActivityRepo = DairyRepoImpl()

        //Load the first page
        loadNext(System.currentTimeMillis(), true)
    }

    init {
        noMoreData.value = false
        activities.value = ArrayList()
    }

    internal fun loadNext(oldestEventTimeMills: Long,
                          isFirstPage: Boolean = false) {
        addDisposable(userActivityRepo
                .loadDaysList(oldestEventTimeMills)
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
                    activities.value?.let {

                        //If there are no items on the list...
                        //Display no data found view
                        if (it.isEmpty()) {
                            val message = ErrorMessage("No data available!!")
                            errorMessage.value = message
                        } else if (it.size.rem(DairyRepo.PAGE_SIZE) != 0) {
                            //If after terminate there not enough item that can fill the page...
                            //Indicates there is no more data to show.
                            noMoreData.value = true
                        }
                    }
                }
                .doOnTerminate {
                    pageLoadingCompleteCallback.call()
                }
                .subscribe({
                    //Update data
                    val newItem = it
                    activities.value?.let {

                        //Check if the month is changed?
                        if (it.isEmpty() || newItem.monthOfYear != it.last().monthOfYear) {
                            //Add the month header.
                            it.add(MonthHeader(newItem.monthOfYear, newItem.year))
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