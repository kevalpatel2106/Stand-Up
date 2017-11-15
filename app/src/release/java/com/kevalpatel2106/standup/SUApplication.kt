package com.kevalpatel2106.standup

import android.app.Application

import timber.log.Timber
import com.filemanager.utils.SharedPrefsProvider

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