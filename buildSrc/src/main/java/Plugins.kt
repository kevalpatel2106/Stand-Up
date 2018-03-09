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
 * Created by Keval on 02/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object Plugins {
    const val buildTools = "com.android.tools.build:gradle:${Versions.buildPlugin}"
    const val jacoco = "com.vanniktech:gradle-android-junit-jacoco-plugin:0.11.0"
    const val dexcount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.2"
    const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val dokka = "org.jetbrains.dokka:dokka-android-gradle-plugin:${Versions.dokka}"
    const val versionScan = "com.github.ben-manes:gradle-versions-plugin:0.17.0"
    const val buildScan = "com.gradle:build-scan-plugin:1.11"
    const val googleServices = "com.google.gms:google-services:3.2.0"
    const val firbase = "com.google.firebase:firebase-plugins:1.1.5"
    const val fabric = "io.fabric.tools:gradle:1.25.1"
    const val testLogger = "com.adarshr:gradle-test-logger-plugin:1.1.2"

    val modulePlugins = arrayListOf(
            "kotlin-android",
            "kotlin-kapt",
            "kotlin-android-extensions",
            "com.vanniktech.android.junit.jacoco",
            "com.getkeepsafe.dexcount",
            "org.jetbrains.dokka",
            "com.adarshr.test-logger"
    )
}
