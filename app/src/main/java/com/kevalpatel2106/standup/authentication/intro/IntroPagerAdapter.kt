package com.kevalpatel2106.standup.authentication.intro

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.kevalpatel2106.base.annotations.UIController
import com.kevalpatel2106.standup.R

/**
 * Created by Keval on 19/11/17.
 * Adapter to bind [IntroFragment] with the view pager to display intro screens.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@UIController
internal class IntroPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    //TODO Add the proper captions
    @Suppress("PrivatePropertyName")
    private val INTRO_CAPTION = arrayOf(R.string.tag_line,
            R.string.tag_line,
            R.string.tag_line)

    //TODO Add the proper intro images
    @Suppress("PrivatePropertyName")
    private val INTRO_IMAGE = arrayOf(R.mipmap.ic_launcher,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher)

    /**
     * Return the Fragment associated with a specified position.
     */
    override fun getItem(position: Int): Fragment = IntroFragment.newInstance(INTRO_CAPTION[position], INTRO_IMAGE[position])

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int = Math.min(INTRO_CAPTION.size, INTRO_IMAGE.size)

}