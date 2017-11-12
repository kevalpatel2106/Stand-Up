package com.kevalpatel2106.standup

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * This activity will take the user information like weight, height and sleep hours while user
 * opens app for the first time.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class UserInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)
    }
}
