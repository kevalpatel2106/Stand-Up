package com.kevalpatel2106.standup.diary.list

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.annotations.ViewModel
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.db.DailyActivitySummary
import com.kevalpatel2106.standup.diary.repo.DairyRepo
import com.kevalpatel2106.standup.diary.repo.DairyRepoImpl

/**
 * Created by Keval on 23/12/17.
 * View model for [com.kevalpatel2106.standup.diary.DiaryFragment].
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
                .doOnSubscribe({
                    noMoreData.value = false
                    if (isFirstPage) blockUi.value = true
                })
                .doOnNext {
                    //Do not block UI.
                    blockUi.value = false
                }
                .subscribe({
                    //Update data
                    val fullList = activities.value
                    fullList?.add(it)
                    activities.value = fullList
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