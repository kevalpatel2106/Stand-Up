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

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Keval on 11/01/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SettingsItemTest {

    @Test
    @Throws(Exception::class)
    fun checkTitle() {
        val data = SettingsItem(SettingsId.NOTIFICATION, "Title 1", 0)
        Assert.assertEquals("Title 1", data.title)
        Assert.assertEquals(SettingsId.NOTIFICATION, data.id)
    }

    @Test
    @Throws(Exception::class)
    fun checkEmptyTitle() {
        try {
            SettingsItem(SettingsId.NOTIFICATION, "", 0)
            Assert.fail()
        } catch (e: IllegalArgumentException) {
            //Test passed
        }
    }

    @Test
    @Throws(Exception::class)
    fun checkIcon() {
        val data = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        Assert.assertEquals(0, data.icon)
    }

    @Test
    @Throws(Exception::class)
    fun checkEqualsWithDifferentTitle() {
        val data1 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data2 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data3 = SettingsItem(SettingsId.NOTIFICATION,"Title 2", 0)

        Assert.assertEquals(data1, data2)
        Assert.assertNotEquals(data1, data3)
        Assert.assertNotEquals(data2, data3)
    }

    @Test
    @Throws(Exception::class)
    fun checkEqualsWithDifferentId() {
        val data1 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data2 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data3 = SettingsItem(SettingsId.DND,"Title 1", 0)

        Assert.assertEquals(data1, data2)
        Assert.assertNotEquals(data1, data3)
        Assert.assertNotEquals(data2, data3)
    }

    @Test
    @Throws(Exception::class)
    fun checkHashCodeEquals() {
        val data1 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data2 = SettingsItem(SettingsId.NOTIFICATION,"Title 1", 0)
        val data3 = SettingsItem(SettingsId.NOTIFICATION,"Title 2", 0)

        Assert.assertEquals(data1.hashCode(), data2.hashCode())
        Assert.assertNotEquals(data1.hashCode(), data3.hashCode())
        Assert.assertNotEquals(data2.hashCode(), data3.hashCode())
    }


}