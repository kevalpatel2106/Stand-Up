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

import android.graphics.Color
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TimeLineViewTest {

    @Test
    fun checkSettingTimelineItems() {
        val timeline = TimeLineView(RuntimeEnvironment.application)
        timeline.labelColor = Color.GREEN

        Assert.assertEquals(Color.GREEN, timeline.labelColor)
        Assert.assertEquals(Color.GREEN, timeline.labelTextPaint.color)
    }

    @Test
    fun checkSettingDefaultTimelineDuration() {
        val timeline = TimeLineView(RuntimeEnvironment.application)
        Assert.assertEquals(TimeLineConfig.DEFAULT_DURATION, timeline.timelineDuration)
    }

    @Test
    fun checkSettingTimelineDuration() {
        val timeline = TimeLineView(RuntimeEnvironment.application)
        timeline.timelineDuration = TimeLineLength.A_DAY
        Assert.assertEquals(TimeLineLength.A_DAY, timeline.timelineDuration)

        //TODO check indicator block list
        //TODO check block calculate
    }
}