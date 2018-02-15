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

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.VisibleForTesting
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View

/**
 * Created by Kevalpatel2106 on 15-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@SuppressLint("ViewConstructor")
class TimeLineView @JvmOverloads constructor(context: Context,
                                             attrs: AttributeSet? = null,
                                             defStyleAttr: Int = 0,
                                             defStyleRes: Int = 0)
    : View(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * Height of the view.
     */
    private var viewHeight: Int = 0

    /**
     * Width of the view.
     */
    private var viewWidth: Int = 0

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
                it.calculateYBound(viewWidth)
            }

            //Generate labels
            labels = Utils.prepareLabels(viewWidth, value)

            invalidate()
        }

    /**
     * Time line items to display.
     */
    var timelineData = listOf<TimeLineData>()
        set(value) {

            //Precess the input data and calculate the start and end coordinates.
            value.filter {
                it.timelineItems.isNotEmpty()
            }.forEach {
                        //calculate the start and end coordinates.
                        it.calculateXBound(viewWidth, timelineDuration)
                        it.calculateYBound(viewWidth)
                    }

            field = value

            //Refresh the list
            invalidate()
        }

    private var timeLineDataPaint: Paint
    private var labelPaint: Paint

    init {
        attrs?.let {
            //TODO Parse the attribute
        }

        //Prepare block color
        timeLineDataPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        //Prepare the label pain
        labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        labelPaint.textSize = 30F
        labelPaint.color = Color.WHITE
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
            it.calculateYBound(viewHeight)
        }

        //Generate labels
        labels = Utils.prepareLabels(viewWidth, timelineDuration)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Draw x axes
        canvas.drawLine(
                x,
                y + viewHeight - TimeLineConfig.LABLE_AREA_HEIGHT - 2,
                x + viewWidth,
                y + viewHeight - TimeLineConfig.LABLE_AREA_HEIGHT + 2,
                labelPaint
        )

        //Draw labels
        labels.forEach {
            canvas.save()
            canvas.rotate(-45F,
                    it.x,
                    viewHeight.toFloat())

            canvas.drawText(it.title,
                    it.x,
                    viewHeight.toFloat(),
                    labelPaint)

            canvas.restore()

            canvas.drawLine(
                    it.x - 1,
                    y,
                    it.x + 1,
                    y + viewHeight - TimeLineConfig.LABLE_AREA_HEIGHT,
                    labelPaint
            )
        }

        //Draw the timeline data
        for (data in timelineData) {
            timeLineDataPaint.color = data.color

            //Draw timeline item for the data.
            data.timelineItems.forEach {
                canvas.drawRect(x + it.startX, y + data.startY, x + it.endX, y + data.endY, timeLineDataPaint)
            }
        }
    }
}
