/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kevalpatel2106.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Keval on 19-Jul-17.
 * Test class for [SharedPrefsProvider].
 */
@RunWith(AndroidJUnit4::class)
class SharedPrefsProviderTest : BaseTestClass() {

    private var mSharedPreferences: SharedPreferences? = null
    private val TEST_KEY = "test_key"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        SharedPrefsProvider.init(InstrumentationRegistry.getContext().applicationContext)
        mSharedPreferences = InstrumentationRegistry.getContext().getSharedPreferences("app_prefs",
                Context.MODE_PRIVATE)

        //Clear the preference.
        val editor = mSharedPreferences!!.edit()
        editor.clear()
        editor.apply()
    }

    @Test
    @Throws(Exception::class)
    fun removePreferences() {
        val editor = mSharedPreferences!!.edit()
        editor.putString(TEST_KEY, "String")
        editor.apply()

        SharedPrefsProvider.removePreferences(TEST_KEY)
        assertTrue(mSharedPreferences!!.getString(TEST_KEY, null) == null)
    }

    @Test
    @Throws(Exception::class)
    fun savePreferences() {
        assertFalse(mSharedPreferences!!.getInt(TEST_KEY, -1) != -1)
        SharedPrefsProvider.savePreferences(TEST_KEY, "String")
        assertTrue(mSharedPreferences!!.getString(TEST_KEY, null) != null)
    }

    @Test
    @Throws(Exception::class)
    fun savePreferences1() {
        assertFalse(mSharedPreferences!!.getInt(TEST_KEY, -1) != -1)
        SharedPrefsProvider.savePreferences(TEST_KEY, 1)
        assertTrue(mSharedPreferences!!.getInt(TEST_KEY, -1) != -1)
    }

    @Test
    @Throws(Exception::class)
    fun savePreferences2() {
        assertFalse(mSharedPreferences!!.getLong(TEST_KEY, -1) != -1L)
        SharedPrefsProvider.savePreferences(TEST_KEY, 100000L)
        assertTrue(mSharedPreferences!!.getLong(TEST_KEY, -1) != -1L)
    }

    @Test
    @Throws(Exception::class)
    fun savePreferences3() {
        assertFalse(mSharedPreferences!!.getBoolean(TEST_KEY, false))
        SharedPrefsProvider.savePreferences(TEST_KEY, true)
        assertTrue(mSharedPreferences!!.getBoolean(TEST_KEY, false))
    }

    @Test
    @Throws(Exception::class)
    fun getStringFromPreferences() {
        val testVal = "String"

        val editor = mSharedPreferences!!.edit()
        editor.putString(TEST_KEY, testVal)
        editor.apply()

        assertTrue(SharedPrefsProvider.getStringFromPreferences(TEST_KEY) == testVal)
    }

    @Test
    @Throws(Exception::class)
    fun getBoolFromPreferences() {
        val testVal = true

        val editor = mSharedPreferences!!.edit()
        editor.putBoolean(TEST_KEY, true)
        editor.apply()


        assertTrue(SharedPrefsProvider.getBoolFromPreferences(TEST_KEY) == testVal)
    }

    @Test
    @Throws(Exception::class)
    fun getLongFromPreference() {
        val testVal = 100000L

        val editor = mSharedPreferences!!.edit()
        editor.putLong(TEST_KEY, testVal)
        editor.apply()

        assertTrue(SharedPrefsProvider.getLongFromPreference(TEST_KEY) == testVal)
    }

    @Test
    @Throws(Exception::class)
    fun getIntFromPreference() {
        val testVal = 100

        val editor = mSharedPreferences!!.edit()
        editor.putInt(TEST_KEY, testVal)
        editor.apply()

        assertTrue(SharedPrefsProvider.getIntFromPreference(TEST_KEY) == testVal)
    }

    override fun getActivity(): Activity? = null
}
