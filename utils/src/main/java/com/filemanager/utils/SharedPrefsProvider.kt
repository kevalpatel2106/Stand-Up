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

package com.filemanager.utils

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by Keval on 20-Aug-16.
 * Class contains all the helper functions to access shared prefs.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
class SharedPrefsProvider(context: Context) {

    /**
     * Name of the shared preference file.
     */
    private val PREF_FILE = "app_prefs"

    /**
     * shared preference object.
     */
    private val mSharedPreference: SharedPreferences

    init {
        mSharedPreference = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
    }

    /**
     * Remove and clear data from preferences for given field

     * @param key key of preference field to remove
     */
    fun removePreferences(key: String) {
        //Delete SharedPref
        val prefsEditor = mSharedPreference.edit()
        prefsEditor.remove(key)
        prefsEditor.apply()
    }

    /**
     * Save value to shared preference

     * @param key   key of preference field
     * *
     * @param value value to store
     */
    fun savePreferences(key: String, value: String?) {
        //Save to share prefs
        val prefsEditor = mSharedPreference.edit()
        prefsEditor.putString(key, value)
        prefsEditor.apply()
    }

    /**
     * Save value to shared preference

     * @param key   key of preference field
     * *
     * @param value value to store
     */
    fun savePreferences(key: String, value: Boolean) {
        //Save to share prefs
        val prefsEditor = mSharedPreference.edit()
        prefsEditor.putBoolean(key, value)
        prefsEditor.apply()
    }

    /**
     * Save value to shared preference

     * @param key   key of preference field
     * *
     * @param value value to store in int
     */
    fun savePreferences(key: String, value: Int) {
        //Save to share prefs
        val prefsEditor = mSharedPreference.edit()
        prefsEditor.putInt(key, value)
        prefsEditor.apply()
    }


    /**
     * Save value to shared preference

     * @param key   key of preference field
     * *
     * @param value value to store in long
     */
    fun savePreferences(key: String, value: Long) {
        //Save to share prefs
        val prefsEditor = mSharedPreference.edit()
        prefsEditor.putLong(key, value)
        prefsEditor.apply()
    }

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return string value for given key else null if key not found.
     */
    fun getStringFromPreferences(key: String): String? = mSharedPreference.getString(key, null)

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return string value for given key else null if key not found.
     */
    fun getStringFromPreferences(key: String, defVal: String): String? = mSharedPreference.getString(key, defVal)

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return boolean value for given key else flase if key not found.
     */
    fun getBoolFromPreferences(key: String): Boolean = mSharedPreference.getBoolean(key, false)

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return boolean value for given key else flase if key not found.
     */
    fun getBoolFromPreferences(key: String, defVal: Boolean): Boolean = mSharedPreference.getBoolean(key, defVal)

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return long value for given key else -1 if key not found.
     */
    fun getLongFromPreference(key: String): Long = mSharedPreference.getLong(key, (-1).toLong())

    /**
     * Read string from shared preference file

     * @param key : key of value field to read
     * *
     * @return int value for given key else -1 if key not found.
     */
    fun getIntFromPreference(key: String): Int = mSharedPreference.getInt(key, -1)

}
