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

/**
 * Created by Kevalpatel2106 on 15-Dec-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object TimeLineConfig {

    //Defaults
    val DEFAULT_DURATION = TimeLineLength.A_DAY
    const val DEFAULT_INDICATOR_COLOR = Color.WHITE
    const val DEFAULT_LABEL_TEXT_COLOR = Color.WHITE
    const val DEFAULT_AXIS_COLOR = Color.WHITE

    const val LABEL_AREA_HEIGHT = 105

    const val INDICATOR_WIDTH = 1F
    const val AXIS_WIDTH = 4F
}
