package com.kevalpatel2106.standup

import android.app.Application
import android.os.StrictMode
import com.facebook.FacebookSdk
import com.facebook.stetho.Stetho
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.standup.db.StandUpDb
import com.kevalpatel2106.utils.SharedPrefsProvider
import com.squareup.leakcanary.LeakCanary
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

        initDebugTools()

        initNetworkModule()

        //Initialize shared preference
        SharedPrefsProvider.init(this)

        setUpFirebase()

        //Initialize db
        StandUpDb.init(this@SUApplication)

        //Initialize facebook
        @Suppress("DEPRECATION")
        FacebookSdk.sdkInitialize(this@SUApplication)
    }

    private fun setUpFirebase() {
        //Initialize firebase.
        FirebaseApp.initializeApp(this@SUApplication)

        //Disable firebase analytics
        FirebaseAnalytics.getInstance(this@SUApplication).setAnalyticsCollectionEnabled(false)
    }

    private fun initNetworkModule() {
        //Init shetho
        Stetho.initializeWithDefaults(this)

        //Initialize the api module
        ApiProvider.init(this@SUApplication)
    }

    private fun initDebugTools() {
        //Enable strict mode
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build())

        //Enable timber
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return (super.createStackElementTag(element) + ":" + element.methodName
                        + " ->L" + element.lineNumber)
            }
        })

        //Initialize the leak canary
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
    }
}
