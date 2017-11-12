/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.network;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by Keval Patel on 10/09/17.
 * Function to retry for the particular number of times with interval.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
final class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
    private final int mMaxRetries;
    private final int mRetryInMills;
    private int mRetryCount;

    /**
     * Public constructor.
     *
     * @param maxRetries       Maximum number of retries.
     * @param retryDelayMillis Interval between
     */
    RetryWithDelay(final int maxRetries, final int retryDelayMillis) {
        mMaxRetries = maxRetries;
        mRetryInMills = retryDelayMillis;

        mRetryCount = 0;
    }

    @Override
    public Observable<?> apply(final Observable<? extends Throwable> attempts) {
        return attempts.flatMap((Function<Throwable, Observable<?>>) throwable -> {
            if (throwable instanceof UnknownHostException && ++mRetryCount <= mMaxRetries) {
                // When this Observable calls onNext, the original
                // Observable will be retried (i.e. re-subscribed).
                return Observable.timer(mRetryInMills, TimeUnit.MILLISECONDS);
            }

            // Max retries hit. Just pass the error along.
            return Observable.error(throwable);
        });
    }
}
