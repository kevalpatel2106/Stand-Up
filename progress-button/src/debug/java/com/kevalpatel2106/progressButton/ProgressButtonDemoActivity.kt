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

package com.kevalpatel2106.progressButton

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.kevalpatel2106.progressButton.customViews.ProgressButton

/**
 * Created by Keval on 07/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */

class ProgressButtonDemoActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_button_demo)

        val progressButton = findViewById<ProgressButton>(R.id.progress_button_demo_1)

        progressButton.setPaddingProgress(20F)
        progressButton.setSpinningBarColor(Color.RED)

        progressButton.setOnClickListener {
            if (!progressButton.isLoading) {
                progressButton.startAnimation()
            } else {
                progressButton.revertAnimation()
            }
        }
    }
}