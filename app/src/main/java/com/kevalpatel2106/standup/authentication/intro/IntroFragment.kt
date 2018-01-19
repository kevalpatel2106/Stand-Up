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

package com.kevalpatel2106.standup.authentication.intro


import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.common.base.uiController.BaseFragment
import com.kevalpatel2106.standup.R
import com.kevalpatel2106.utils.annotations.UIController
import kotlinx.android.synthetic.main.fragment_intro.*


/**
 * A simple [Fragment] subclass to display the introduction text and image into the [IntroActivity].
 *
 * @see [IntroPagerAdapter]
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
class IntroFragment : BaseFragment() {

    companion object {
        private val ARG_CAPTION = "ARG_CAPTION"
        private val ARG_IMAGE = "ARG_IMAGE"

        /**
         * Get the new instance of the fragment [IntroFragment].
         *
         * @param caption String resource for the text to display as the caption.
         * @param image Drawable resource for the image to display.
         * @return [IntroFragment]
         */
        fun newInstance(@StringRes caption: Int, @DrawableRes image: Int): IntroFragment {
            val introFragment = IntroFragment()

            val bundle = Bundle()
            bundle.putInt(ARG_CAPTION, caption)
            bundle.putInt(ARG_IMAGE, image)
            introFragment.arguments = bundle

            return introFragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            intro_tv.setText(arguments!!.getInt(ARG_CAPTION))
            intro_iv.setImageResource(arguments!!.getInt(ARG_IMAGE))
        }
    }

}
