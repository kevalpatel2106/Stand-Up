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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bignerdranch.expandablerecyclerview.ParentViewHolder
import com.standup.app.settings.R
import kotlinx.android.synthetic.main.row_instruction_parent.view.*

/**
 * Created by Kevalpatel2106 on 12-Mar-18.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
internal class InstructionParentViewHolder(itemView: View) : ParentViewHolder<Instruction, String>(itemView) {

    companion object {

        fun create(context: Context, container: ViewGroup): InstructionParentViewHolder {
            return InstructionParentViewHolder(LayoutInflater.from(context)
                    .inflate(R.layout.row_instruction_parent, container, false))
        }
    }

    fun bind(instruction: Instruction) {
        itemView.instruction_title_tv.text = instruction.heading
        itemView.instruction_title_iv.setImageResource(instruction.icon)
    }
}
