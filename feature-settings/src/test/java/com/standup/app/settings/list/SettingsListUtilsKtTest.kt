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

import com.danielstone.materialaboutlibrary.items.MaterialAboutItemOnClickAction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 12-Mar-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SettingsListUtilsKtTest {

    private val title = 7243
    private val subTitle = 2342
    private val icon = 2243

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_Title() {
        val card = prepareCard(icon, title)
        Assert.assertEquals(title, card.textRes)
        Assert.assertNull(card.text)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_Icon() {
        val card = prepareCard(icon, title)
        Assert.assertEquals(icon, card.iconRes)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_WithoutSubTitle() {
        val card = prepareCard(icon, title)
        Assert.assertEquals(0, card.subTextRes)
        Assert.assertNull(card.subText)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_SubTitle() {
        val card = prepareCard(icon, title, subTitle)
        Assert.assertEquals(subTitle, card.subTextRes)
        Assert.assertNull(card.subText)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_WithoutClickListener() {
        val card = prepareCard(icon, title, subTitle)
        Assert.assertNull(card.onClickAction)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_WithClickListener() {
        val clickActionListener = MaterialAboutItemOnClickAction {
            //Do nothing
        }
        val card = prepareCard(icon, title, subTitle, clickActionListener)
        Assert.assertEquals(clickActionListener, card.onClickAction)
    }

    @Test
    @Throws(Exception::class)
    fun checkPrepareCard_ClickListener_CheckClick() {
        var isClicked = false

        val card = prepareCard(icon, title, subTitle, MaterialAboutItemOnClickAction {
            isClicked = true
        })

        Assert.assertFalse(isClicked)
        card.onClickAction.onClick()
        Assert.assertTrue(isClicked)
    }
}
