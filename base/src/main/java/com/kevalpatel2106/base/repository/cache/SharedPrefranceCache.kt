package com.kevalpatel2106.base.repository.cache

import android.content.SharedPreferences

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
abstract class SharedPrefranceCache<T>(protected val sharedPreferences: SharedPreferences) : Cache<T>