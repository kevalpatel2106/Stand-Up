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

package com.kevalpatel2106.standup.about.report

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.base.arch.BaseViewModel
import com.kevalpatel2106.base.arch.ErrorMessage
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.standup.about.repo.AboutRepository
import com.kevalpatel2106.standup.about.repo.AboutRepositoryImpl
import com.kevalpatel2106.standup.about.repo.CheckVersionResponse
import com.kevalpatel2106.standup.authentication.repo.UserAuthRepository
import com.kevalpatel2106.standup.misc.Validator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class ReportIssueViewModel : BaseViewModel {
    internal val versionUpdateResult = MutableLiveData<CheckVersionResponse>()

    internal val issueId = MutableLiveData<Long>()

    /**
     * Repository to provide user authentications.
     */
    @VisibleForTesting
    internal var mAuthRepository: AboutRepository

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param authRepository Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(authRepository: AboutRepository) : super() {
        this.mAuthRepository = authRepository
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() : super() {
        //This is the original user authentication repo.
        mAuthRepository = AboutRepositoryImpl()
    }

    fun checkForUpdate() {
        addDisposable(mAuthRepository.getLatestVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    it?.let { versionUpdateResult.value = it }
                }, {
                    versionUpdateResult.value = null
                    errorMessage.value = ErrorMessage(R.string.check_update_error_message)
                }))
    }

    fun reportIssue(title: String, message: String, deviceId : String) {
        blockUi.value = true

        //Check title
        if (!Validator.isValidIssueTitle(title)) {
            blockUi.value = false
            errorMessage.value = ErrorMessage(R.string.error_invalid_issue_title)
            return
        }

        //Check description
        if (!Validator.isValidIssueDescription(message)) {
            blockUi.value = false
            errorMessage.value = ErrorMessage(R.string.error_invalid_issue_description)
            return
        }

        //Check device id
        if (!Validator.isValidDeviceId(deviceId)) {
           throw IllegalArgumentException("Invalid device id.")
        }

        //Report the issue
        addDisposable(mAuthRepository.reportIssue(title, message, deviceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { blockUi.value = true }
                .doOnTerminate { blockUi.value = false }
                .subscribe({
                    it?.let { issueId.value = it.issueId }
                }, {
                    errorMessage.value = ErrorMessage(R.string.error_message_report_issue)
                }))
    }
}