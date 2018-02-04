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

package com.standup.core

import com.evernote.android.job.util.JobLogger
import timber.log.Timber

/**
 * Created by Keval on 24/01/18.
 * Custom [JobLogger] which user [Timber] to print the jobs reheated to scheduling or canceling the
 * jobs.
 * See [How can I add a custom logger?](https://github.com/evernote/android-job/wiki/FAQ#how-can-i-add-a-custom-logger)
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class EvernoteJobLogger : JobLogger {

    override fun log(priority: Int, tag: String, message: String, t: Throwable?) {
        t?.let { Timber.e(t.message) }
        Timber.i("Job $tag is scheduled. $message")
    }
}