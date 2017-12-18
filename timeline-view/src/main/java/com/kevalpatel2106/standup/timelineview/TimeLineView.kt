package com.kevalpatel2106.standup.timelineview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.RequiresApi
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.kevalpatel2106.utils.ViewUtils

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

    private val PADDING_VERTICAL = 50
    private val PADDING_HORIZONTAL = 20

    private val ctx: Context

    /**
     * Time line items to display.
     */
    private var timelineItems = ArrayList<TimeLineItem>()

    /**
     * [TextPaint] to display the axis labels.
     */
    private lateinit var labelTextPaint: TextPaint

    private lateinit var timeLineBlockPaint: Paint

    lateinit var timelineLength: TimeLineLength

    private var viewHeight: Int = 0
    private var viewWidth: Int = 0

    /**
     * Color to display the axis labels.
     */
    @ColorInt
    var labelColor: Int = TimeLineConfig.DEFAULT_LABEL_COLOR
        set(value) {
            field = value
            refreshLabelTextPaint()
            invalidate()
        }

    private fun init(attrs: AttributeSet? = null) {
        attrs?.let {
            //TODO Parse the attribute
        }

        timelineLength = TimeLineConfig.DEFAULT_DURATION

        //Prepare the label text
        labelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        refreshLabelTextPaint()

        //Prepare block color
        timeLineBlockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    fun setTimeLineItems(items: ArrayList<TimeLineItem>) {
        timelineItems = items
        calculateTimeLineSlots(viewWidth)
        invalidate()
    }

    private fun refreshLabelTextPaint() {
        labelTextPaint.textSize = ViewUtils.toPx(ctx, TimeLineConfig.DEFAULT_LABEL_TEXT_COLOR).toFloat()
        labelTextPaint.color = labelColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Get measured height of the view.
        viewHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        viewWidth = View.MeasureSpec.getSize(widthMeasureSpec)

        this.setMeasuredDimension(viewWidth, viewHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        calculateTimeLineSlots(viewWidth)
    }

    private fun calculateTimeLineSlots(width: Int) {
        val timeLineLengthSec = Utils.convertTimeLineLengthToSeconds(timelineLength)
        val eachSecondWidth = width.toFloat() / timeLineLengthSec

        timelineItems.forEach {
            it.startX = it.startTime * eachSecondWidth
            it.endX = it.endTime * eachSecondWidth
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        timelineItems.forEach {
            timeLineBlockPaint.color = it.color
            canvas.drawRect(it.startX, y, it.endX, y + viewHeight, timeLineBlockPaint)
        }
    }
}
