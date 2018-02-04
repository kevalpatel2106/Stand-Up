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

package com.standup.app.about.repo

import com.kevalpatel2106.common.UserSessionManager
import com.kevalpatel2106.common.application.di.AppModule
import com.kevalpatel2106.common.repository.RepoBuilder
import com.kevalpatel2106.network.executor.refresher.RetrofitNetworkRefresher
import io.reactivex.Flowable
import retrofit2.Retrofit
import javax.inject.Named

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class AboutRepositoryImpl(@Named(AppModule.WITH_TOKEN) private val retrofit: Retrofit,
                          private val userSessionManager: UserSessionManager) : AboutRepository {

    /**
     * Get the latest version of the android application.
     *
     * It calls [AboutApiService.getLatestVersion] endpoint to call the server and returns the response
     * as [CheckVersionResponse]. In the response [CheckVersionResponse.isUpdate] true indicates that,
     * the application has update available. [CheckVersionResponse.latestVersionName] contains the
     * latest version name.
     *
     * @see AboutApiService.getLatestVersion
     * @see CheckVersionRequest
     * @see CheckVersionResponse
     */
    override fun getLatestVersion(): Flowable<CheckVersionResponse> {
        val checkVersionRequest = CheckVersionRequest()
        val call = retrofit.create(AboutApiService::class.java).getLatestVersion(checkVersionRequest)

        return RepoBuilder<CheckVersionResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }
    }

    /**
     * Report new issue for the application based on issue [title] and the issue [message]. The
     * unique [deviceId] is passed along with other device information such as [ReportIssueRequest.deviceId]
     * and [ReportIssueRequest.deviceName].
     *
     * This function will internally call [AboutApiService.reportIssue]
     * endpoint and send the issue data to the server. The response [ReportIssueResponse] contains
     * [ReportIssueResponse.issueId] to uniquely identify the issue later on.
     *
     * @see AboutApiService.reportIssue
     * @see ReportIssueRequest
     * @see ReportIssueResponse
     */
    override fun reportIssue(title: String, message: String, deviceId: String): Flowable<ReportIssueResponse> {
        val reportIssueRequest = ReportIssueRequest(title, message, deviceId)
        val call = retrofit.create(AboutApiService::class.java).reportIssue(reportIssueRequest)

        return RepoBuilder<ReportIssueResponse>()
                .addRefresher(RetrofitNetworkRefresher(call))
                .build()
                .fetch()
                .map { t -> t.data }
    }
}
