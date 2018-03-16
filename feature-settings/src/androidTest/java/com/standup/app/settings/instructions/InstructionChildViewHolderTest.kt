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

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.kevalpatel2106.common.view.BaseTextView
import com.standup.app.settings.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 16-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class InstructionChildViewHolderTest {

    @Test
    @Throws(Exception::class)
    fun checkCreate() {
        val holder = InstructionChildViewHolder.create(
                context = InstrumentationRegistry.getContext(),
                container = RelativeLayout(InstrumentationRegistry.getContext())
        )

        Assert.assertNotNull(holder)
        Assert.assertNotNull(holder.itemView.findViewById<BaseTextView>(R.id.instruction_message_tv))
    }

    @Test
    @Throws(Exception::class)
    fun checkBind() {
        val itemView = LayoutInflater.from(InstrumentationRegistry.getContext())
                .inflate(R.layout.row_instruction_child, null)

        val holder = InstructionChildViewHolder(itemView)

        holder.bind("test-string")

        Assert.assertEquals(itemView.findViewById<BaseTextView>(R.id.instruction_message_tv).text,
                "test-string")
    }
}
