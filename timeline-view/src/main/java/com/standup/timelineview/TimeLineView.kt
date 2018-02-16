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

package com.standup.timelineview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.annotation.VisibleForTesting
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

/**
 * Created by Kevalpatel2106 on 15-Dec-17.
 * TODO Add touch listener.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@SuppressLint("ViewConstructor")
class TimeLineView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0,
                                             defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes) {

    private val labelAreaHeight = TimeLineConfig.getLabelAreaHeight(context)

    /**
     * Height of the view.
     */
    private var viewHeight: Int = 0

    /**
     * Width of the view.
     */
    private var viewWidth: Int = 0

    /**
     * Boolean to set true to enable touch events on the view.
     *
     * @see [onTouchEvent]
     */
    var enableTouch: Boolean = true

    private var touchBubbleX: Float = 0F
    private var touchTime: String? = null

    //Animations
    private var valueAnimator: ValueAnimator? = null
    private var heightAnimationFactor: Float = 0F

    @VisibleForTesting
    internal var labels = listOf<Label>()

    /**
     * Duration of the timeline.
     *
     * @see TimeLineLength
     */
    var timelineDuration: TimeLineLength = TimeLineConfig.DEFAULT_DURATION
        set(value) {
            field = value

            //Precess the input data and calculate the start and end coordinates.
            timelineData.forEach {
                //calculate the start and end coordinates.
                it.calculateXBound(viewWidth, value)
                it.calculateYBound(context, viewWidth)
            }

            //Generate labels
            labels = Utils.prepareLabels(viewWidth, value)

            invalidate()
        }

    /**
     * Time line items to display.
     */
    var timelineData = arrayListOf<TimeLineData>()
        set(value) {

            //Precess the input data and calculate the start and end coordinates.
            value.filter {
                it.timelineItems.isNotEmpty()
            }.forEach {
                        //calculate the start and end coordinates.
                        it.calculateXBound(viewWidth, timelineDuration)
                        it.calculateYBound(context, viewWidth)
                    }

            Collections.sort(value) { p0, p1 -> p1.heightPercentage - p0.heightPercentage }

            field = value

            //Refresh the list
            startAnimation()

            invalidate()
        }

    private fun startAnimation() {
        valueAnimator?.cancel()

        valueAnimator = ValueAnimator.ofFloat(0F, 1F)
        valueAnimator!!.duration = 1000L
        valueAnimator!!.startDelay = 800L
        valueAnimator!!.addUpdateListener {
            heightAnimationFactor = it.animatedFraction
            requestLayout()
        }
        valueAnimator!!.start()
    }

    //Paints
    private var timeLineDataPaint: Paint
    private var labelPaint: Paint
    private var axesPaint: Paint
    private var indicatorPaint: Paint
    private var touchLabelPaint: Paint

    init {
        attrs?.let {
            //TODO Parse the attribute
        }

        //Prepare block color
        timeLineDataPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        //Prepare the label pain
        labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        labelPaint.textSize = TimeLineConfig.getLabelTextHeight(context)
        labelPaint.color = TimeLineConfig.DEFAULT_LABEL_TEXT_COLOR

        //Prepare the label pain
        touchLabelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        touchLabelPaint.textSize = TimeLineConfig.getBubbleLabelTextHeight(context)
        touchLabelPaint.color = TimeLineConfig.DEFAULT_LABEL_TEXT_COLOR

        //Prepare the axis pain
        axesPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        axesPaint.color = TimeLineConfig.DEFAULT_AXIS_COLOR

        //Prepare the milestone indicator pain
        indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        indicatorPaint.color = TimeLineConfig.DEFAULT_INDICATOR_COLOR
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Get measured height of the view.
        viewHeight = View.MeasureSpec.getSize(heightMeasureSpec)
        viewWidth = View.MeasureSpec.getSize(widthMeasureSpec)

        this.setMeasuredDimension(viewWidth, viewHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //Update the list coordinates
        timelineData.forEach {
            it.calculateXBound(viewWidth, timelineDuration)
            it.calculateYBound(context, viewHeight)
        }

        //Generate labels
        labels = Utils.prepareLabels(viewWidth, timelineDuration)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var isBubbleDisplayed = false

        //Draw the timeline data
        for (data in timelineData) {
            timeLineDataPaint.color = data.color

            //Draw timeline item for the data.
            val topY = y + data.startY
            val endY = y + data.endY
            data.timelineItems.forEach {
                canvas.drawRect(
                        x + it.startX,
                        topY,
                        x + it.endX,
                        endY,
                        timeLineDataPaint
                )

                if (touchBubbleX in x + it.startX..x + it.endX) {
                    drawTouchBubbleWithLabel(canvas, y + data.startY)
                    isBubbleDisplayed = true
                }
            }
        }

        //Draw labels
        labels.forEach {
            canvas.save()
            canvas.rotate(-90F,
                    it.x,
                    viewHeight.toFloat())

            //Draw the label
            canvas.drawText(it.title,
                    it.x + (labelAreaHeight - labelPaint.measureText(it.title)) / 2,
                    viewHeight.toFloat() + 15F,
                    labelPaint)

            canvas.restore()

            //Draw the indicator.
            canvas.drawLine(
                    it.x - TimeLineConfig.INDICATOR_WIDTH / 2,
                    y,
                    it.x + TimeLineConfig.INDICATOR_WIDTH / 2,
                    y + viewHeight - labelAreaHeight,
                    indicatorPaint
            )
        }

        //Draw x axes
        canvas.drawLine(x,
                y + viewHeight - labelAreaHeight - TimeLineConfig.AXIS_WIDTH / 2,
                x + viewWidth,
                y + viewHeight - labelAreaHeight + TimeLineConfig.AXIS_WIDTH / 2,
                axesPaint
        )
        if (!isBubbleDisplayed) {
            drawTouchBubbleWithLabel(canvas, y + viewHeight - labelAreaHeight)
        }
    }

    private fun drawTouchBubbleWithLabel(canvas: Canvas, touchBubbleY: Float) {
        if (enableTouch && touchBubbleX > 0F) {
            canvas.drawCircle(touchBubbleX, touchBubbleY, TimeLineConfig.TOUCH_BUBBLE_RADIUS, labelPaint)

            with(labelPaint.measureText(touchTime) + TimeLineConfig.TOUCH_BUBBLE_LABEL_X_OFFSET) {
                if (this + touchBubbleX > x + viewWidth) {
                    canvas.drawText("$touchTime",
                            touchBubbleX - this - TimeLineConfig.TOUCH_BUBBLE_LABEL_X_OFFSET,
                            touchBubbleY - TimeLineConfig.TOUCH_BUBBLE_LABEL_Y_OFFSET,
                            touchLabelPaint)
                } else {
                    canvas.drawText("$touchTime",
                            touchBubbleX + TimeLineConfig.TOUCH_BUBBLE_LABEL_X_OFFSET,
                            touchBubbleY - TimeLineConfig.TOUCH_BUBBLE_LABEL_Y_OFFSET,
                            touchLabelPaint)
                }
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                //If touch is not in touchable region...
                //If the touch bubble is visible, remove it.
                hideTouchBubble()
            }
            MotionEvent.ACTION_DOWN -> {
                //Check if the user touched in valid area.
                if (handleBubbleMove(event)) return true
            }
            MotionEvent.ACTION_MOVE -> {
                //Check if the user touched in valid area.
                if (handleBubbleMove(event)) return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun handleBubbleMove(event: MotionEvent): Boolean {
        if (enableTouch && Utils.isTouchable(touchX = event.x,
                        touchY = event.y,
                        viewX = x,
                        viewY = x,
                        viewWidth = viewWidth,
                        viewHeight = viewHeight,
                        labelAreaHeight = labelAreaHeight)) {

            touchBubbleX = event.x
            touchTime = Utils.getTouchLabel(event.x, viewWidth, timelineDuration)
            invalidate()
            return true /* Event handled */
        } else {

            //If touch is not in touchable region...
            //If the touch bubble is visible, remove it.
            hideTouchBubble()
        }
        return false
    }

    private fun hideTouchBubble() {
        if (touchBubbleX > 0) {
            touchBubbleX = 0F
            invalidate()
        }
    }
}
