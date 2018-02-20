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

package com.standup.app.diary.userActivityList

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.application.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.db.userActivity.UserActivity
import com.kevalpatel2106.common.misc.LottieJson
import com.kevalpatel2106.utils.TimeUtils
import com.kevalpatel2106.utils.annotations.ViewModel
import com.standup.app.diary.R
import com.standup.app.diary.detail.DetailActivity
import com.standup.app.diary.di.DaggerDiaryComponent
import com.standup.app.diary.repo.DiaryRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Keval on 22/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@ViewModel(DetailActivity::class)
internal class UserActivityListModel : BaseViewModel {

    @Inject
    internal lateinit var diaryRepo: DiaryRepo

    @VisibleForTesting
    constructor(diaryRepo: DiaryRepo) {
        this.diaryRepo = diaryRepo
        init()
    }

    @Suppress("unused")
    constructor() {
        DaggerDiaryComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@UserActivityListModel)
        init()
    }

    internal val userActivities = MutableLiveData<ArrayList<UserActivity>>()

    fun init() {
        userActivities.value = ArrayList()
    }

    fun fetchData(dayOfMonth: Int, month: Int, year: Int) {
        addDisposable(diaryRepo.loadUserActivityByDay(dayOfMonth, month, year)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe {
                    blockUi.value = true
                }
                .doOnTerminate {
                    blockUi.value = false
                }
                .subscribe({

                    if (it.isEmpty()) {
                        val errorMsg = ErrorMessage("No user activities found for $dayOfMonth ${TimeUtils.getMonthInitials(month)} $year")
                        errorMsg.setErrorBtn(R.string.error_view_btn_title_retry, {
                            fetchData(dayOfMonth, month, year)
                        })
                        errorMsg.errorImage = LottieJson.CLOUD_FLOATING
                        errorMessage.value = errorMsg
                    } else {
                        userActivities.value?.clear()
                        userActivities.value?.addAll(it)
                        userActivities.value = userActivities.value
                    }
                }, {
                    val errorMsg = ErrorMessage(it.message)
                    errorMsg.setErrorBtn(R.string.error_view_btn_title_retry, {
                        fetchData(dayOfMonth, month, year)
                    })
                    errorMsg.errorImage = LottieJson.UFO_FLOATING
                    errorMessage.value = errorMsg
                }))
    }
}

