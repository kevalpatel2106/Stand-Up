package com.kevalpatel2106.standup

import android.app.Application
import com.kevalpatel2106.utils.SharedPrefsProvider
import timber.log.Timber

/**
 * Created by Keval on 07-11-17.
 *
 * Application class for the release application. This will initialize the timber tree.
 */

class SUApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        //Enable timber
        Timber.plant(ReleaseTree())

        //Initialize shared preference
        SharedPrefsProvider.init(this)
    }
}