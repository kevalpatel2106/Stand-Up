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

package com.kevalpatel2106.standup.constants

import android.graphics.Color
import android.support.annotation.ColorInt

/**
 * Created by Keval on 17/11/17.
 * This class contains configuration variables for application.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal object AppConfig {

    /**
     * Pagination item limit in whole list.
     */
    const val PAGE_LIMIT = 20

    /**
     * Snackbar display duration.
     */
    const val SNACKBAR_TIME = 3300L //ms

    /**
     * Animation time for the Pie chart.
     */
    const val PIE_CHART_TIME = 1400 //ms

    //---- End of Validation ----//

    //---- Start of Timeline config ----//
    @ColorInt
    const val COLOR_SITTING = Color.RED
    @ColorInt
    const val COLOR_STANDING = Color.GREEN
    @ColorInt
    const val COLOR_NOT_TRACKED = Color.TRANSPARENT
    //---- End of Timeline config ----//

    const val GENDER_MALE = "male"
    const val GENDER_FEMALE = "female"
}
