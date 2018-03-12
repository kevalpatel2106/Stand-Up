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
import android.view.ViewGroup
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter

/**
 * Created by Keval on 11/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal class InstructionAdapter(private val context: Context,
                                  instruction: ArrayList<Instruction>)
    : ExpandableRecyclerAdapter<Instruction, String, InstructionParentViewHolder, InstructionChildViewHolder>(instruction) {

    init {
        setExpandCollapseListener(object : ExpandCollapseListener {
            internal var lastExpandedPosition = -1

            override fun onParentExpanded(parentPosition: Int) {
                //Collapse the last expanded item
                if (lastExpandedPosition >= 0 && parentPosition != lastExpandedPosition)
                    collapseParent(lastExpandedPosition)

                lastExpandedPosition = parentPosition
            }

            override fun onParentCollapsed(parentPosition: Int) {
                //Do nothing
            }
        })
    }

    override fun onCreateParentViewHolder(
            parentViewGroup: ViewGroup,
            viewType: Int
    ): InstructionParentViewHolder {
        return InstructionParentViewHolder.create(context, parentViewGroup)
    }

    override fun onBindChildViewHolder(
            childViewHolder: InstructionChildViewHolder,
            parentPosition: Int,
            childPosition: Int,
            child: String
    ) {
        childViewHolder.bind(child)
    }

    override fun onBindParentViewHolder(
            parentViewHolder: InstructionParentViewHolder,
            parentPosition: Int,
            parent: Instruction
    ) {
        parentViewHolder.bind(parent)
    }

    override fun onCreateChildViewHolder(
            childViewGroup: ViewGroup,
            viewType: Int
    ): InstructionChildViewHolder {
        return InstructionChildViewHolder.create(context, childViewGroup)
    }
}
