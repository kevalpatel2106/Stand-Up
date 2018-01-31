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

package com.kevalpatel2106.standup.misc

import android.content.Context
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.airbnb.lottie.LottieAnimationView
import com.kevalpatel2106.common.base.arch.ErrorMessage
import com.kevalpatel2106.common.view.BaseButton
import com.kevalpatel2106.common.view.BaseTextView
import com.kevalpatel2106.standup.R

/**
 * Created by Keval on 24/12/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class ErrorView @JvmOverloads constructor(context: Context,
                                          attrs: AttributeSet? = null,
                                          defStyleAttr: Int = 0)
    : LinearLayout(context, attrs, defStyleAttr) {

    private val errorImageView: LottieAnimationView

    private val errorTextView: BaseTextView

    private val errorButton: BaseButton

    init {
        inflate(context, R.layout.layout_error, this@ErrorView)

        errorImageView = findViewById(R.id.error_iv)
        errorTextView = findViewById(R.id.error_tv)
        errorButton = findViewById(R.id.error_btn)
    }


    fun setError(errorMessage: ErrorMessage) {
        setError(errorMessage.getMessage(context)!!,
                errorMessage.errorImage,
                errorMessage.getErrorBtnText(),
                errorMessage.getOnErrorClick())
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setError(errorMessage: String,
                 errorImage: String? = null,
                 @StringRes errorBtnTitle: Int = 0,
                 errorClick: (() -> Unit)? = null) {
        errorTextView.text = errorMessage

        //set error image
        if (errorImage == null) {
            errorImageView.visibility = View.GONE
        } else {
            errorImageView.visibility = View.VISIBLE
            errorImageView.playRepeatAnimation(errorImage)
        }

        if (errorClick == null || errorBtnTitle == 0) {
            errorButton.visibility = View.GONE
        } else {
            errorButton.visibility = View.VISIBLE
            errorButton.text = context.getString(errorBtnTitle)
            errorButton.setOnClickListener({ errorClick.invoke() })
        }
    }
}
