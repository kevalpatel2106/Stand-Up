package com.kevalpatel2106.standup.constants

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

    //---- Start of Validation ----//
    const val MIN_WEIGHT = 29.9f
    const val MAX_WEIGHT = 204f

    const val MIN_HEIGHT = 116f
    const val MAX_HEIGHT = 264f

    const val MAX_PASSWORD = 16
    const val MIN_PASSWORD = 6

    const val MIN_NAME = 6
    const val MAX_NAME = 30
    //---- End of Validation ----//

    const val GENDER_MALE = "male"
    const val GENDER_FEMALE = "female"
}