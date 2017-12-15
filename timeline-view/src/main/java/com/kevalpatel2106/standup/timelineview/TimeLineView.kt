package com.kevalpatel2106.standup.timelineview

import android.content.Context
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

    private val ctx: Context

    /**
     * Total duration of the time line in seconds.
     */
    var durationSec: Long = TimeLineConfig.DEFAULT_DURATION

    /**
     * [TextPaint] to display the axis labels.
     */
    private lateinit var labelTextPaint: TextPaint

    /**
     * [Paint] for the base axis.
     */
    private lateinit var baseAxisPaint: Paint

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

    /**
     * Color for the base axis.
     */
    @ColorInt
    var baseAxisColor: Int = TimeLineConfig.DEFAULT_AXIS_COLOR
        set(value) {
            field = value
            refreshBaseAxisPaint()
            invalidate()
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

    private fun init(attrs: AttributeSet? = null) {
        attrs?.let {
            //TODO Parse the attribute
        }

        //Prepare the label text
        labelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        refreshLabelTextPaint()

        //Draw the base axis
        baseAxisPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        refreshBaseAxisPaint()

    }

    private fun refreshBaseAxisPaint() {
        baseAxisPaint.color = baseAxisColor
    }

    private fun refreshLabelTextPaint() {
        labelTextPaint.textSize = ViewUtils.toPx(ctx, TimeLineConfig.DEFAULT_LABEL_TEXT_COLOR).toFloat()
        labelTextPaint.color = labelColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Get measured height of the view.
        val viewHeight = View.MeasureSpec.getSize(widthMeasureSpec)
        val viewWidth = View.MeasureSpec.getSize(heightMeasureSpec)

        this.setMeasuredDimension(viewWidth, viewHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
