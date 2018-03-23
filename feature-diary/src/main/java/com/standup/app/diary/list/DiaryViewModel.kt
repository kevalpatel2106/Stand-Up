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

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.CallbackEvent
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.misc.lottie.LottieJson
import com.kevalpatel2106.common.userActivity.DailyActivitySummary
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.billing.repo.BillingRepo
import com.standup.app.diary.R
import com.standup.app.diary.di.DaggerDiaryComponent
import com.standup.app.diary.repo.DiaryRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
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

    @Inject
    lateinit var application: Application

    @Inject
    lateinit var billingRepo: BillingRepo

    /**
     * Private constructor to add the custom [DiaryRepo] for testing.
     *
     * @param diaryRepo Add your own [DiaryRepo].
     */
    @VisibleForTesting
    constructor(
            application: Application,
            diaryRepo: DiaryRepo,
            billingRepo: BillingRepo
    ) : super() {
        this.application = application
        this.userActivityRepo = diaryRepo
        this.billingRepo = billingRepo

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

    val moreItemsBlocked = MutableLiveData<Boolean>()

    val pageLoadingCompleteCallback = CallbackEvent()

    private fun init() {
        noMoreData.value = false
        moreItemsBlocked.value = false
        activities.value = ArrayList()

        //Load the first page
        loadDailySummaryPage(System.currentTimeMillis(), true)
    }


    /**
     * Load the next page with list of [DailyActivitySummary] which has [DailyActivitySummary.startTimeMills]
     * less than [oldestEventTimeMills] to display in the list.
     *
     * [oldestEventTimeMills] represents the oldest event ending time you have in the previous list.
     * If you are loading the first page  of the list, [System.currentTimeMillis] will be
     * [oldestEventTimeMills].
     *
     * If the application is loading the first page of the list [isFirstPage] will be set to true.
     *
     * This will emit [DailyActivitySummary] item one-by-one in onNext() and it will keep
     * emitting the items until [DiaryRepo.PAGE_SIZE] number of items emitted or no more daily
     * summary left.
     */
    internal fun loadDailySummaryPage(oldestEventTimeMills: Long,
                                      isFirstPage: Boolean = false) {
        addDisposable(billingRepo.isPremiumPurchased(application)
                .observeOn(Schedulers.io())     //Cannot do DB operation on main thread in flatMap.
                .subscribeOn(Schedulers.io())
                .filter {
                    if (it || isFirstPage /* Premium user or the first page. */) {
                        moreItemsBlocked.postValue(false)
                        return@filter true
                    } else {
                        moreItemsBlocked.postValue(true)
                        noMoreData.postValue(true)
                        return@filter false
                    }
                }
                .flatMapObservable {
                    // Emit the observer for daily activity summary
                    return@flatMapObservable userActivityRepo.loadDaysSummaryList(
                            oldestEventTimeMills - 1
                    ).toObservable()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe({
                    noMoreData.value = false
                    if (isFirstPage) blockUi.value = true
                })
                .doOnNext {
                    if (isFirstPage) {
                        //Do not block UI.
                        blockUi.value = false
                    }
                }
                .doOnTerminate {
                    blockUi.value = false
                    pageLoadingCompleteCallback.dispatch()
                }
                .doOnComplete {
                    activities.value?.let {

                        //If there are no items on the list...
                        //Display no data found view
                        if (it.isEmpty()) {

                            val message = ErrorMessage(R.string.error_diary_list_no_items_available)
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
                .subscribe({

                    //Update data
                    @Suppress("UnnecessaryVariable")
                    val newItem = it
                    activities.value?.let {

                        //Add the month header.
                        if (it.isEmpty()) {

                            //It's the first item
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
                    Timber.e(it.printStackTrace().toString())

                    val message = ErrorMessage(it.message)
                    message.errorImage = LottieJson.PERSON_FALLING_FROM_UFO
                    message.setErrorBtn(R.string.error_view_btn_title_retry, {

                        //Call this function again
                        loadDailySummaryPage(oldestEventTimeMills, isFirstPage)
                    })
                    errorMessage.value = message
                }))
    }

}
