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

/**
 * Created by Keval Patel on 02/03/18.
 * List of all the dependencies used in the project.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object Deps {

    //Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val testRule = "com.android.support.test:rules:1.0.2-alpha1"
    const val archTesting = "android.arch.core:core-testing:${Versions.archComponent}"
    const val testRunner = "com.android.support.test:runner:1.0.2-alpha1"
    const val espressoCore = "com.android.support.test.espresso:espresso-core:${Versions.espresso}"
    const val espressoIntents = "com.android.support.test.espresso:espresso-intents:${Versions.espresso}"
    const val espressoContrib = "com.android.support.test.espresso:espresso-contrib:${Versions.espresso}"
    const val espressoIdlingResource = "com.android.support.test.espresso:espresso-idling-resource:${Versions.espresso}"
    const val uiAutomator = "com.android.support.test.uiautomator:uiautomator-v18:${Versions.uiAutomator}"
    const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.okHttp}"
    const val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    const val roomTesting = "android.arch.persistence.room:testing:${Versions.room}"


    //Support lib
    const val annotations = "com.android.support:support-annotations:${Versions.supportLib}"
    const val supportv4 = "com.android.support:support-v4:${Versions.supportLib}"
    const val appCompat = "com.android.support:appcompat-v7:${Versions.supportLib}"
    const val recyclerView = "com.android.support:recyclerview-v7:${Versions.supportLib}"
    const val cardView = "com.android.support:cardview-v7:${Versions.supportLib}"
    const val design = "com.android.support:design:${Versions.supportLib}"
    const val customtabs = "com.android.support:customtabs:${Versions.supportLib}"
    const val prefrance = "com.android.support:preference-v7:${Versions.supportLib}"
    const val prefrancev14 = "com.android.support:preference-v14:${Versions.supportLib}"
    const val multidex = "com.android.support:multidex:${Versions.multidex}"
    const val gridLayout = "com.android.support:gridlayout-v7:${Versions.supportLib}"

    //Architecture components
    const val room = "android.arch.persistence.room:runtime:${Versions.room}"
    const val roomAnnotation = "android.arch.persistence.room:compiler:${Versions.room}"
    const val lifecycleJava8 = "android.arch.lifecycle:common-java8:${Versions.archComponent}"
    const val lifecycle = "android.arch.lifecycle:extensions:${Versions.archComponent}"

    //Firebase
    const val firebaseCore = "com.google.firebase:firebase-core:${Versions.playService}"
    const val fcm = "com.google.firebase:firebase-messaging:${Versions.playService}"
    const val firebasePreformance = "com.google.firebase:firebase-perf:${Versions.playService}"
    const val remoteConfig = "com.google.firebase:firebase-config:${Versions.playService}"
    const val firebaseInvites = "com.google.firebase:firebase-invites:${Versions.playService}"

    //Play service
    const val googleAuth = "com.google.android.gms:play-services-auth:${Versions.playService}"
    const val location = "com.google.android.gms:play-services-location:${Versions.playService}"
    const val awareness = "com.google.android.gms:play-services-awareness:${Versions.playService}"

    //Other libs
    const val gson = "com.google.code.gson:gson:${Versions.gson}"
    const val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    const val glideAnnotation = "com.github.bumptech.glide:compiler:${Versions.glide}"
    const val circleImagView = "de.hdodenhof:circleimageview:2.2.0"
    const val facebook = "com.facebook.android:facebook-android-sdk:${Versions.facebook}"
    const val bottomsheet = "com.cocosw:bottomsheet:1.3.1"
    const val commonsCodec = "commons-codec:commons-codec:1.11"
    const val crashalytics = "com.crashlytics.sdk.android:crashlytics:2.9.1@aar"
    const val openSourceLibraries = "com.mikepenz:aboutlibraries:6.0.6@aar"
    const val aboutlibraries = "com.github.daniel-stoneuk:material-about-library:2.2.5"
    const val chartLayout = "com.github.PhilJay:MPAndroidChart:v3.0.3"
    const val roboElectric = "org.robolectric:robolectric:3.7.1"
    const val evernoteJob = "com.evernote:android-job:1.2.4"
    const val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
    const val iap = "com.android.billingclient:billing:1.0"
    const val bottomsheetPicker = "com.philliphsu:bottomsheetpickers:2.4.1"
    const val ringtonePicker = "com.kevalpatel2106:ringtonepicker:1.1"
    const val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constrainLayout}"

    //Kotlin libs
    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val kotlinRefflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

    //Retrofit + OkHttp
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitGsonPlugin = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val httpLogger = "com.squareup.okhttp3:logging-interceptor:${Versions.okHttp}"

    //RxJava
    const val rxJava = "io.reactivex.rxjava2:rxjava:${Versions.rxJava}"
    const val rxAndroid = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"

    //Libs for debug puropose
    const val debugDb = "com.amitshekhar.android:debug-db:1.0.3"
    const val timber = "com.jakewharton.timber:timber:4.6.1"
    const val leakCanary = "com.squareup.leakcanary:leakcanary-android:1.5.4"

    //Dagger dependencies
    const val daggerAndroid = "com.google.dagger:dagger-android:${Versions.daggerVersion}"
    const val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.daggerVersion}"
    const val daggerAnnotations = "com.google.dagger:dagger-android-processor:${Versions.daggerVersion}"
    const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.daggerVersion}"
    const val jsr250 = "javax.annotation:jsr250-api:1.0"
}
