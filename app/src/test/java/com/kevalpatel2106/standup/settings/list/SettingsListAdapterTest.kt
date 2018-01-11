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

package com.kevalpatel2106.standup.settings.list

import android.content.Context
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import java.io.IOException

/**
 * Created by Keval on 11/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SettingsListAdapterTest {

    private lateinit var data: Array<SettingsItem>
    private lateinit var adapter: SettingsListAdapter

    @Before
    fun setUp() {
        data = arrayOf(
                SettingsItem("Title 1", 1),
                SettingsItem("Title 2", 2),
                SettingsItem("Title 3", 3),
                SettingsItem("Title 4", 4),
                SettingsItem("Title 5", 5)
        )

        val context = Mockito.mock(Context::class.java)
        adapter = SettingsListAdapter(context, data, null)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetItemCount() {
        Assert.assertEquals(data.size, adapter.itemCount)
    }

    @Test
    @Throws(IOException::class)
    fun checkGetItem() {
        Assert.assertEquals(data[0], adapter.getItem(0))
        Assert.assertEquals(data[1], adapter.getItem(1))
        Assert.assertEquals(data[2], adapter.getItem(2))
        Assert.assertEquals(data[3], adapter.getItem(3))
    }
}