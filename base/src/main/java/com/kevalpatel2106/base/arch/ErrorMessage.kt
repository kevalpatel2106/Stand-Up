package com.kevalpatel2106.base.arch

import android.content.Context
import android.support.annotation.StringRes
import android.support.annotation.VisibleForTesting

/**
 * Created by Kevalpatel2106 on 30-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ErrorMessage {

    private val errorMessage: String?

    @VisibleForTesting
    @StringRes
    val errorRes: Int

    constructor(@StringRes errorRes: Int) {
        errorMessage = null
        this.errorRes = errorRes
    }

    constructor(errorMessage: String?) {
        this.errorMessage = errorMessage
        this.errorRes = 0
    }

    fun getMessage(context: Context?): String? {
        return errorMessage ?: context?.getString(errorRes)
    }
}