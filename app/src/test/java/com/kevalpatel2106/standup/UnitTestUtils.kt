package com.kevalpatel2106.standup

import android.content.Context
import android.content.SharedPreferences
import com.kevalpatel2106.network.ApiProvider
import com.kevalpatel2106.utils.SharedPrefsProvider
import org.mockito.ArgumentMatchers
import org.mockito.Mockito

/**
 * Created by Keval on 23/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object UnitTestUtils {


    fun initApp() {
        val context = Mockito.mock(Context::class.java)

        val sharedPrefs = Mockito.mock(SharedPreferences::class.java)
        val sharedPrefsEditor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(context.getSharedPreferences(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(sharedPrefs)
        Mockito.`when`(sharedPrefs.edit()).thenReturn(sharedPrefsEditor)

        SharedPrefsProvider.init(context)

        //Init server
        ApiProvider.init()
    }
}