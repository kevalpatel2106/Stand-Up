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
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListAdapter
import com.standup.app.settings.R
import kotlinx.android.synthetic.main.row_instruction_child.view.*
import kotlinx.android.synthetic.main.row_instruction_parent.view.*

/**
 * Created by Keval on 11/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class InstructionAdapter(private val context: Context,
                         private val instruction: ArrayList<Instruction>) : ExpandableListAdapter {


    override fun getChildrenCount(parentPos: Int): Int = 1

    override fun getGroup(groupPos: Int): Instruction = instruction[groupPos]

    override fun isEmpty(): Boolean = false

    override fun registerDataSetObserver(p0: DataSetObserver?) {
        //Do nothing
    }

    override fun getChild(groupPos: Int, childPos: Int): String {
        return instruction[groupPos].message
    }

    override fun onGroupExpanded(p0: Int) {
        //Do nothing
    }

    override fun onGroupCollapsed(p0: Int) {
        //Do nothing
    }

    override fun getCombinedChildId(p0: Long, p1: Long): Long = p0 + p1

    override fun getGroupId(groupPos: Int): Long = instruction[groupPos].heading.hashCode().toLong()

    override fun isChildSelectable(p0: Int, p1: Int): Boolean = false

    override fun hasStableIds(): Boolean = true

    override fun getChildView(
            groupPos: Int,
            childPos: Int,
            isExpanded: Boolean,
            convertView: View?,
            container: ViewGroup?
    ): View {
        val view = LayoutInflater.from(context).inflate(R.layout.row_instruction_child, container, false)

        view.instruction_message_tv.text = getGroup(groupPos).heading
        return view
    }

    override fun areAllItemsEnabled(): Boolean = true

    override fun getChildId(groupPos: Int, childPos: Int): Long = instruction[groupPos].message.hashCode().toLong()

    override fun getCombinedGroupId(groupPos: Long): Long = groupPos

    override fun getGroupView(
            groupPosition: Int,
            isExpanded: Boolean,
            convertView: View?,
            container: ViewGroup?
    ): View {
        val view = LayoutInflater.from(context).inflate(R.layout.row_instruction_parent, container, false)

        view.instruction_title_tv.text = getGroup(groupPosition).heading
        view.instruction_title_iv.setImageResource(getGroup(groupPosition).icon)

        return view
    }

    override fun unregisterDataSetObserver(p0: DataSetObserver?) {
        //Do nothing
    }

    override fun getGroupCount(): Int = instruction.size

}