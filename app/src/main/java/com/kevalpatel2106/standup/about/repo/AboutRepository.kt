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

package com.kevalpatel2106.standup.about.repo

import com.kevalpatel2106.utils.annotations.Repository
import io.reactivex.Flowable

/**
 * Created by Kevalpatel2106 on 29-Dec-17.
 * Abstract implementation of [com.kevalpatel2106.common.annotations.Repository] for the about module.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Repository
interface AboutRepository {

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
    fun getLatestVersion(): Flowable<CheckVersionResponse>

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
    fun reportIssue(title: String, message: String, deviceId: String): Flowable<ReportIssueResponse>
}
