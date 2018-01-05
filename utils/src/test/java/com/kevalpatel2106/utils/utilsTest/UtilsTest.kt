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

package com.kevalpatel2106.utils.utilsTest

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import com.kevalpatel2106.utils.Utils
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 22-Nov-17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@RunWith(JUnit4::class)
class UtilsTest {

    /**
     * Test for [Utils.getDeviceName].
     */
    @Test
    @Throws(Exception::class)
    fun getDeviceName() {
        assertNotNull(Utils.getDeviceName())
        assertTrue(Utils.getDeviceName().contains("-"))
        assertEquals(Utils.getDeviceName(), String.format("%s-%s", Build.MANUFACTURER, Build.MODEL))
    }

    @Test
    fun checkGetApplicationName() {
        val packageManager = Mockito.mock(PackageManager::class.java)
        val applicationInfo = Mockito.mock(ApplicationInfo::class.java)
        Mockito.`when`(packageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(applicationInfo)
        Mockito.`when`(packageManager.getApplicationLabel(applicationInfo)).thenReturn("Test Data")

        Assert.assertEquals("Test Data", Utils
                .getApplicationName("com.test.package", packageManager))
    }

    @Test
    fun checkGetApplicationNameIfApplicationInfoNull() {
        val packageManager = Mockito.mock(PackageManager::class.java)
        Mockito.`when`(packageManager.getApplicationInfo(anyString(), anyInt())).thenReturn(null)

        Assert.assertNull(Utils.getApplicationName("com.test.package", packageManager))
    }

    @Test
    fun checkGetNameOfNotInstalledApplication() {
        val packageManager = Mockito.mock(PackageManager::class.java)
        Mockito.`when`(packageManager.getApplicationInfo(anyString(), anyInt()))
                .thenThrow(PackageManager.NameNotFoundException())

        Assert.assertNull(Utils.getApplicationName("com.test.package", packageManager))
    }

    @Test
    fun checkIsNetworkAvailableWhenNetworkNotAvailable() {
        val context = Mockito.mock(Context::class.java)
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val activeNetworkInfo = Mockito.mock(NetworkInfo::class.java)

        Mockito.`when`(context.getSystemService(anyString())).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(activeNetworkInfo)
        Mockito.`when`(activeNetworkInfo.isConnected).thenReturn(false)

        Assert.assertEquals(Utils.isNetworkAvailable(context), false)
    }

    @Test
    fun checkIsNetworkAvailableWhenNetworkAvailable() {
        val context = Mockito.mock(Context::class.java)
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)
        val activeNetworkInfo = Mockito.mock(NetworkInfo::class.java)

        Mockito.`when`(context.getSystemService(anyString())).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(activeNetworkInfo)
        Mockito.`when`(activeNetworkInfo.isConnected).thenReturn(true)

        Assert.assertEquals(Utils.isNetworkAvailable(context), true)
    }

    @Test
    fun checkIsNetworkAvailableWhenNetworkInfoNull() {
        val context = Mockito.mock(Context::class.java)
        val connectivityManager = Mockito.mock(ConnectivityManager::class.java)

        Mockito.`when`(context.getSystemService(anyString())).thenReturn(connectivityManager)
        Mockito.`when`(connectivityManager.activeNetworkInfo).thenReturn(null)

        Assert.assertEquals(Utils.isNetworkAvailable(context), false)
    }
}