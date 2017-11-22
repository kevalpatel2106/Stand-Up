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

package com.kevalpatel2106.testutils

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * Created by Keval on 21-Jul-17.
 * This is the test activity to load fragments inside it while running the instrumentation tests.
 */

class FragmentRuleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val frameLayout = FrameLayout(this@FragmentRuleActivity)
        setContentView(frameLayout)

        frameLayout.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        frameLayout.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        frameLayout.id = R.id.container
    }
}
