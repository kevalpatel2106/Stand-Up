package com.kevalpatel2106.testutils

import android.content.Context
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.toPackage
import android.support.test.espresso.matcher.ViewMatchers.withId


/**
 * Created by Kevalpatel2106 on 21-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object InstrumentationTestUtils {

    fun checkActivityOnViewClicked(context: Context,
                                   viewId: Int,
                                   nextActivityName: String) {
        Intents.init()

        //set intent package
        intending(toPackage("com.kevalpatel2106.standup"))

        // Perform click operation on FAB
        onView(withId(viewId)).perform(click())

        // Assert that if the opened activity is Quiz activity.
        intended(hasComponent(nextActivityName))

        Intents.release()
    }
}