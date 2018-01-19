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

package com.kevalpatel2106.common.base.arch

import android.content.Context
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting

/**
 * Created by Kevalpatel2106 on 30-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ErrorMessage {

    @VisibleForTesting
    internal val errorMessage: String?

    @VisibleForTesting
    @StringRes
    val errorRes: Int

    @DrawableRes
    var errorImage: Int = 0

    @StringRes
    private var errorBtnText: Int = 0

    private var onErrorClick: (() -> Unit)? = null

    constructor(@StringRes errorRes: Int) {
        errorMessage = null
        this.errorRes = errorRes
    }

    constructor(errorMessage: String?) {
        this.errorMessage = errorMessage
        this.errorRes = 0
    }

    fun setErrorBtn(@StringRes errorBtnText: Int, onErrorClick: () -> Unit) {
        this.errorBtnText = errorBtnText
        this.onErrorClick = onErrorClick
    }

    fun getMessage(context: Context?): String? {
        return errorMessage ?: context?.getString(errorRes)
    }

    fun getErrorBtnText() = errorBtnText

    fun getOnErrorClick() = onErrorClick
}
