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

package com.kevalpatel2106.standup.about.repo

import com.kevalpatel2106.base.repository.RepoBuilder
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.network.RetrofitNetworkRefresher
import com.kevalpatel2106.standup.BuildConfig
import com.kevalpatel2106.standup.authentication.repo.UserApiService
import io.reactivex.Flowable

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class AboutRepositoryImpl(private val baseUrl: String) : AboutRepository {

    constructor() : this(UserApiService.baseUrl())

    override fun getLatestVersion(): Flowable<CheckVersionResponse> {
        val checkVersionRequest = CheckVersionRequest(BuildConfig.VERSION_CODE)

        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(AboutApiService::class.java)
                .getLatestVersion(checkVersionRequest)

        return RepoBuilder<CheckVersionResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }
    }

    override fun reportIssue(title: String, message: String): Flowable<ReportIssueResponse> {
        val reportIssueRequest = ReportIssueRequest(title, message)

        val call = ApiProvider.getRetrofitClient(baseUrl)
                .create(AboutApiService::class.java)
                .reportIssue(reportIssueRequest)

        return RepoBuilder<ReportIssueResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }
    }
}