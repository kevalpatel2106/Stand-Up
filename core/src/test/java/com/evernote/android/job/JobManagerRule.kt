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

package com.evernote.android.job

import android.content.Context
import org.junit.rules.ExternalResource


/**
 * Created by Kevalpatel2106 on 27-Feb-18.
 *
 * @see https://github.com/evernote/android-job/blob/master/library/src/test/java/com/evernote/android/job/JobManagerRule.java
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class JobManagerRule(private val jobCreator: JobCreator, private val context: Context) : ExternalResource() {

    lateinit var jobManager: JobManager

    @Throws(Throwable::class)
    override fun before() {
        JobConfig.setSkipJobReschedule(true)

        jobManager = JobManager.create(context)
        jobManager.addJobCreator(jobCreator)
    }

    override fun after() {
        jobManager.cancelAll()
        jobManager.destroy()
        JobConfig.reset()
    }
}
