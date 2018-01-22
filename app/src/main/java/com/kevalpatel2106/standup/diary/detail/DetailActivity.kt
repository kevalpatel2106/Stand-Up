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

package com.kevalpatel2106.standup.diary.detail

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.kevalpatel2106.common.base.uiController.BaseActivity
import com.kevalpatel2106.standup.R

class DetailActivity : BaseActivity() {

    private lateinit var model: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setToolbar(R.id.toolbar, "17 Dec 2017", true)

        model = ViewModelProviders.of(this@DetailActivity).get(DetailViewModel::class.java)
    }

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, DetailActivity::class.java))
        }
    }
}
