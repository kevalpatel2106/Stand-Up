package com.kevalpatel2106.standup.timelineview

import android.support.annotation.ColorInt

/**
 * Created by Kevalpatel2106 on 18-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class TimeLineItem(

        val startTime: Long,

        val endTime: Long,

        @ColorInt
        val color: Int = TimeLineConfig.DEFAULT_BLOCK_BG_COLOR
) {

    internal var startX: Float = 0F

    internal var endX: Float = 0F
}