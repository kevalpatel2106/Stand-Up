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

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Created by Kevalpatel2106 on 04-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(Parameterized::class)
class GetNumberOfIndicatorBlocksTest(private val timeLineLength: TimeLineLength,
                                     private val numOfBlocks: Int) {


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): ArrayList<Array<out Any?>> {
            return arrayListOf(
                    arrayOf(TimeLineLength.AN_HOUR, 6),
                    arrayOf(TimeLineLength.SIX_HOUR, 6),
                    arrayOf(TimeLineLength.TWELVE_HOUR, 12),
                    arrayOf(TimeLineLength.SIXTEEN_HOURS, 16),
                    arrayOf(TimeLineLength.A_DAY, 24)
            )
        }
    }

    @Test
    fun checkConvertTimeLineLengthToSeconds() {
        Assert.assertEquals(Utils.getNumberOfIndicatorBlocks(timeLineLength), numOfBlocks)
    }
}