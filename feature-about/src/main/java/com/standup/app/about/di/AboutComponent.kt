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

package com.standup.app.about.di

import com.kevalpatel2106.common.application.di.AppComponent
import com.kevalpatel2106.common.application.di.ApplicationScope
import com.standup.app.about.AboutViewModel
import com.standup.app.about.report.ReportIssueViewModel
import dagger.Component

/**
 * Created by Kevalpatel2106 on 09-Jan-18.
 * A dagger [Component] for the about module. This [Component] includes [AboutDaggerModule].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see AboutDaggerModule
 */
@ApplicationScope
@Component(dependencies = [AppComponent::class], modules = [AboutDaggerModule::class])
internal interface AboutComponent {

    fun inject(aboutViewModel: AboutViewModel)

    fun inject(reportIssueViewModel: ReportIssueViewModel)
}
