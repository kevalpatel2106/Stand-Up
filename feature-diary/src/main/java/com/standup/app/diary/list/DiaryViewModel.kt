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

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.CallbackEvent
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.db.DailyActivitySummary
import com.kevalpatel2106.common.misc.LottieJson
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.diary.R
import com.standup.app.diary.di.DaggerDiaryComponent
import com.standup.app.diary.repo.DiaryRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 23/12/17.
 * View model for [com.kevalpatel2106.standup.diary.list.DiaryFragment].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@ViewModel(DiaryFragment::class)
internal class DiaryViewModel : BaseViewModel {

    @Inject
    lateinit var userActivityRepo: DiaryRepo

    /**
     * Private constructor to add the custom [DiaryRepo] for testing.
     *
     * @param diaryRepo Add your own [DiaryRepo].
     */
    @VisibleForTesting
    constructor(diaryRepo: DiaryRepo) : super() {
        this.userActivityRepo = diaryRepo

        init()
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerDiaryComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@DiaryViewModel)

        init()
    }


    /**
     * List of user activities.
     */
    val activities = MutableLiveData<ArrayList<DailyActivitySummary>>()

    val noMoreData = MutableLiveData<Boolean>()

    val pageLoadingCompleteCallback = CallbackEvent()

    fun init() {
        noMoreData.value = false
        activities.value = ArrayList()

        //Load the first page
        loadNext(System.currentTimeMillis(), true)
    }

    internal fun loadNext(oldestEventTimeMills: Long,
                          isFirstPage: Boolean = false) {
        addDisposable(userActivityRepo
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
                            message.errorImage = LottieJson.EMPTY_LIST
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
                    message.errorImage = LottieJson.PERSON_FALLING_FROM_UFO
                    message.setErrorBtn(R.string.error_view_btn_title_retry, {

                        //Call this function again
                        loadNext(oldestEventTimeMills, isFirstPage)
                    })
                    errorMessage.value = message
                }))
    }

}
