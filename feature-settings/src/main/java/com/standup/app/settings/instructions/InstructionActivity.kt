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

package com.standup.app.settings.instructions

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.standup.app.settings.R
import kotlinx.android.synthetic.main.activity_instruction.*

class InstructionActivity : AppCompatActivity() {

    companion object {

        fun launch(context: Context) {
            context.startActivity(Intent(context, InstructionActivity::class.java))
        }
    }

    private lateinit var model: InstructionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instruction)

        model = ViewModelProviders.of(this@InstructionActivity).get(InstructionViewModel::class.java)
        instruction_list.layoutManager = LinearLayoutManager(this@InstructionActivity)
        instruction_list.itemAnimator = DefaultItemAnimator()
        instruction_list.adapter = InstructionAdapter(this@InstructionActivity,
                model.instructions.value!!)


        model.instructions.observe(this@InstructionActivity, Observer {
            it?.let { instruction_list.adapter.notifyDataSetChanged() }
        })
    }
}
