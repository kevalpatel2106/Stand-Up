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

package com.standup.app.settings.list

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Context
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito

/**
 * Created by Kevalpatel2106 on 28-Feb-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SettingsViewModelTest {

    private val posSync = 0
    private val posDnd = 1
    private val posDailyReview = 2
    private val posNotification = 3
    private val posLogout = 4
    private val totalSettingsItem = 5

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var model: SettingsViewModel

    @Before
    fun setUp() {
        model = SettingsViewModel()
    }

    @Test
    @Throws(Exception::class)
    fun checkInit() {
        Assert.assertFalse(model.showLogoutConformation.value!!)
        Assert.assertEquals(model.settingsItems.value!!.size, totalSettingsItem)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareSettingsList() {
        model.prepareSettingsList()

        Assert.assertEquals(model.settingsItems.value!!.size, totalSettingsItem)
        Assert.assertEquals(model.settingsItems.value!![posSync].id, SettingsId.SYNC)
        Assert.assertEquals(model.settingsItems.value!![posDnd].id, SettingsId.DND)
        Assert.assertEquals(model.settingsItems.value!![posDailyReview].id, SettingsId.DAILY_REVIEW)
        Assert.assertEquals(model.settingsItems.value!![posNotification].id, SettingsId.NOTIFICATION)
        Assert.assertEquals(model.settingsItems.value!![posLogout].id, SettingsId.LOGOUT)
    }


    @Test
    @Throws(Exception::class)
    fun checkSettingsClick_Logout_SinglePane() {
        Assert.assertFalse(model.showLogoutConformation.value!!)

        val mockContext = Mockito.mock(Context::class.java)
        model.onSettingsClicked(mockContext,
                SettingsItem(SettingsId.LOGOUT, "Test", 54),
                false)

        Assert.assertTrue(model.showLogoutConformation.value!!)

        //Check selection
        Assert.assertFalse(model.settingsItems.value!![posSync].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posDnd].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posDailyReview].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posNotification].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posLogout].isSelected)
    }

    @Test
    @Throws(Exception::class)
    fun checkSettingsClick_Logout_TwoPane() {
        Assert.assertFalse(model.showLogoutConformation.value!!)

        val mockContext = Mockito.mock(Context::class.java)
        model.onSettingsClicked(mockContext,
                SettingsItem(SettingsId.LOGOUT, "Test", 54),
                true)

        Assert.assertTrue(model.showLogoutConformation.value!!)

        //Check selection
        Assert.assertFalse(model.settingsItems.value!![posSync].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posDnd].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posDailyReview].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posNotification].isSelected)
        Assert.assertFalse(model.settingsItems.value!![posLogout].isSelected)
    }

}
