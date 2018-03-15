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

package com.standup.app.about.report

import android.arch.lifecycle.MutableLiveData
import android.support.annotation.VisibleForTesting
import com.kevalpatel2106.common.Validator
import com.kevalpatel2106.common.base.BaseApplication
import com.kevalpatel2106.common.base.arch.BaseViewModel
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.standup.app.about.R
import com.standup.app.about.di.DaggerAboutComponent
import com.standup.app.about.repo.AboutRepository
import com.standup.app.about.repo.CheckVersionResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class ReportIssueViewModel : BaseViewModel {
    /**
     * Repository to provide user authentications.
     */
    @Inject
    lateinit var aboutRepository: AboutRepository

    /**
     * Private constructor to add the custom [UserAuthRepository] for testing.
     *
     * @param authRepository Add your own [UserAuthRepository].
     */
    @Suppress("unused")
    @VisibleForTesting
    constructor(authRepository: AboutRepository) {
        this.aboutRepository = authRepository
    }

    /**
     * Zero parameter constructor.
     */
    @Suppress("unused")
    constructor() {
        DaggerAboutComponent.builder()
                .appComponent(BaseApplication.getApplicationComponent())
                .build()
                .inject(this@ReportIssueViewModel)
    }

    internal val versionUpdateResult = MutableLiveData<CheckVersionResponse>()

    internal val issueId = MutableLiveData<Long>()

    /**
     * Check for the new version update to the server. If the new update is available
     * [versionUpdateResult] will return new version info. [errorMessage] will contain the error
     * detail if any error occurs.
     */
    fun checkForUpdate() {
        addDisposable(aboutRepository.getLatestVersion()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    it?.let {
                        if (it.isUpdate) versionUpdateResult.value = it
                    }
                }, {
                    versionUpdateResult.value = null
                    errorMessage.value = ErrorMessage(R.string.check_update_error_message)
                }))
    }

    fun reportIssue(title: String, message: String, deviceId: String) {
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
        addDisposable(aboutRepository.reportIssue(title, message, deviceId)
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
