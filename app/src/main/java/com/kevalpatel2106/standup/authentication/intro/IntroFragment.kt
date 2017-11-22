package com.kevalpatel2106.standup.authentication.intro


import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kevalpatel2106.base.BaseFragment
import com.kevalpatel2106.base.annotations.UIController

import com.kevalpatel2106.standup.R
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
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_intro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            intro_tv.setText(arguments!!.getInt(ARG_CAPTION))
            intro_iv.setImageResource(arguments!!.getInt(ARG_IMAGE))
        }

    }

}
