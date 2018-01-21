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
 */package com.kevalpatel2106.standup.core

import android.support.annotation.WorkerThread
import com.evernote.android.job.Job
import timber.log.Timber
import java.util.concurrent.CountDownLatch

/**
 * Created by Keval on 17/01/18.
 * An extension for [Job] class to perform the asynchronous tasks (such as network operations) inside
 * the [Job].
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see Job
 * @see [How can I run async operations in a job?](https://github.com/evernote/android-job/wiki/FAQ#how-can-i-run-async-operations-in-a-job)
 */
internal abstract class AsyncJob : Job() {
    /**
     * [CountDownLatch] that initialize with the value 1. This latch will keep the job running until
     * the background job completes.
     *
     * @see CountDownLatch.await
     */
    private val countDownLatch = CountDownLatch(1)

    /**
     * Final [com.evernote.android.job.Job.Result] to return from the [onRunJob]
     *
     * @see com.evernote.android.job.Job.Result
     */
    private var result: Result? = null

    /**
     * @see Job.onRunJob
     */
    override fun onRunJob(params: Params): Result {
        onRunJobAsync(params)

        try {
            //Acquire the lock
            countDownLatch.await()
        } catch (ignored: InterruptedException) {
            Timber.w(ignored)
        }

        return result!!
    }

    /**
     * this function will terminate the [Job] and set the [result] for the job.
     */
    protected fun stopJob(result: Result) {
        this.result = result
        countDownLatch.countDown()
    }

    /**
     * Do the task you want to perform in your [Job] here. This tasks may synchronous or asynchronous.
     * [AsyncJob] will assure that you [Job] keeps running in the background when other threads are
     * doing operations. Once you finish your tasks, call [stopJob] and pass the
     * [com.evernote.android.job.Job.Result] in the argument. This will terminate the job.
     *
     * Note: A [android.os.PowerManager.WakeLock] is acquired for 3 minutes for each [Job]. If your
     * task needs more time, then you need to create an extra [android.os.PowerManager.WakeLock].
     *
     * @param params The parameters for this concrete job.
     * @return The result of this [AsyncJob].
     * @see stopJob
     */
    @WorkerThread
    abstract fun onRunJobAsync(params: Params)
}