package com.kevalpatel2106.standup

import android.app.Application
import android.os.StrictMode

import com.facebook.stetho.Stetho
import com.filemanager.utils.SharedPrefsProvider

import timber.log.Timber

/**
 * Created by Keval on 13/11/17.
 *
 * Application class for the debug application. This will initialize the timber tree, strict mode
 * and shetho.
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class SUApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //Enable strict mode
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        //Init shetho
        Stetho.initializeWithDefaults(this)

        //Initialize firebase.
        //TODO Initialize

        //Initialize shared preference
        SharedPrefsProvider.init(this)

        //Enable timber
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return (super.createStackElementTag(element) + ":" + element.methodName
                        + " ->L" + element.lineNumber)
            }
        })
    }
}
