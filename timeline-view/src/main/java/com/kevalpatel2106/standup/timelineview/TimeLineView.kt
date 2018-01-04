/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.timelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * Created by Kevalpatel2106 on 15-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class TimeLineView : View {

    constructor(context: Context) : super(context) {
        this.ctx = context
        init()
    }

    constructor(context: Context,
                attrs: AttributeSet?) : super(context, attrs) {
        this.ctx = context
        init(attrs)
    }

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.ctx = context
        init(attrs)
    }

    @Suppress("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int,
                defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        this.ctx = context
        init(attrs)
    }

    private val ctx: Context

    /**
     * Height of the view.
     */
    private var viewHeight: Int = 0

    /**
     * Width of the view.
     */
    private var viewWidth: Int = 0

    /**
     * Duration of the timeline.
     *
     * @see TimeLineLength
     */
    var timelineDuration: TimeLineLength = TimeLineConfig.DEFAULT_DURATION
        set(value) {
            field = value

            //Update the timeline indicators blocks coordinates.
            timelineIndicatorBlock = Utils.getIndicatorBlockList(value)
            Utils.calculateBlockCoordinates(viewWidth, timelineIndicatorBlock, value)

            invalidate()
        }

    /**
     * Color to display the axis labels.
     */
    @ColorInt
    var labelColor: Int = TimeLineConfig.DEFAULT_LABEL_COLOR
        set(value) {
            field = value

            //Refresh the paint object
            labelTextPaint.color = value

            invalidate()
        }

    /**
     * Time line items to display.
     */
    var timelineItems = ArrayList<TimeLineItem>()
        set(value) {
            field = Utils.calculateBlockCoordinates(viewWidth, value, timelineDuration)

            //Refresh the list
            invalidate()
        }

    /**
     * List of the blocks on the timeline to display as background. The size and values of this list
     * depends on [timelineDuration].
     *
     * @see timelineDuration
     */
    private var timelineIndicatorBlock = ArrayList<TimeLineItem>()

    /**
     * [TextPaint] to display the axis labels.
     */
    @VisibleForTesting
    internal lateinit var labelTextPaint: TextPaint

    private lateinit var timeLineBlockPaint: Paint

    private fun init(attrs: AttributeSet? = null) {
        attrs?.let {
            //TODO Parse the attribute
        }

        //Prepare the label text
        labelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        labelTextPaint.textSize = Utils.toPx(ctx, TimeLineConfig.DEFAULT_LABEL_TEXT_COLOR).toFloat()

        //Prepare block color
        timeLineBlockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Get measured height of the view.
        viewHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        viewWidth = View.MeasureSpec.getSize(widthMeasureSpec)

        this.setMeasuredDimension(viewWidth, viewHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //Update the list coordinates
        Utils.calculateBlockCoordinates(viewWidth, timelineItems, timelineDuration)
        Utils.calculateBlockCoordinates(viewWidth, timelineIndicatorBlock, timelineDuration)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Change the hour colors
        timelineIndicatorBlock.forEach {
            timeLineBlockPaint.color = it.color
            canvas.drawRect(it.startX, y, it.endX, y + viewHeight, timeLineBlockPaint)
        }

        //Display the blocks
        timelineItems.forEach {
            timeLineBlockPaint.color = it.color
            canvas.drawRect(it.startX, y + 70, it.endX, y + +viewHeight, timeLineBlockPaint)
        }
    }
}
