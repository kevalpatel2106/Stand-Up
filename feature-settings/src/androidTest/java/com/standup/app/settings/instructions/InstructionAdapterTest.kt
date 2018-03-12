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
import android.support.v7.widget.RecyclerView
import android.widget.RelativeLayout
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 12-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class InstructionAdapterTest {

    private lateinit var adapter: InstructionAdapter

    @Before
    fun setUp() {
        adapter = InstructionAdapter(InstrumentationRegistry.getContext(), ArrayList())
    }

    @Test
    @Throws(Exception::class)
    fun checkOnCreateParentViewHolder() {
        val parent = RelativeLayout(InstrumentationRegistry.getContext())
        val holder = adapter.onCreateParentViewHolder(parent, 0)

        Assert.assertNotNull(holder)
        Assert.assertFalse(holder.isExpanded)
        Assert.assertEquals(RecyclerView.INVALID_TYPE, holder.itemViewType)
    }

    @Test
    @Throws(Exception::class)
    fun checkOnCreateChildViewHolder() {
        val parent = RelativeLayout(InstrumentationRegistry.getContext())
        val holder = adapter.onCreateChildViewHolder(parent, 0)

        Assert.assertNotNull(holder)
        Assert.assertEquals(RecyclerView.INVALID_TYPE, holder.itemViewType)
    }
}
