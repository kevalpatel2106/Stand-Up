/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.facebookauth

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Kevalpatel2106 on 02-Jan-18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class FacebookUserTest {

    @Test
    fun checkInitWithAllDetail() {

        val facebookUser = FacebookUser(facebookID = "123456789")
        facebookUser.coverPicUrl = "http://coverpic.com"
        facebookUser.profilePic = "http://profilepic.com"
        facebookUser.bio = "This is the test bio"
        facebookUser.about = "This is the test bio"
        facebookUser.gender = "male"
        facebookUser.name = "Test User"
        facebookUser.email = "test@example.com"

        Assert.assertEquals(facebookUser.facebookID, "123456789")
        Assert.assertEquals(facebookUser.coverPicUrl, "http://coverpic.com")
        Assert.assertEquals(facebookUser.profilePic, "http://profilepic.com")
        Assert.assertEquals(facebookUser.bio, "This is the test bio")
        Assert.assertEquals(facebookUser.about, "This is the test bio")
        Assert.assertEquals(facebookUser.gender, "male")
        Assert.assertEquals(facebookUser.name, "Test User")
        Assert.assertEquals(facebookUser.email, "test@example.com")
    }

    @Test
    fun checkInitWithOnlyId() {

        val facebookUser = FacebookUser(facebookID = "123456789")

        Assert.assertEquals(facebookUser.facebookID, "123456789")
        Assert.assertNull(facebookUser.coverPicUrl)
        Assert.assertNull(facebookUser.profilePic)
        Assert.assertNull(facebookUser.bio)
        Assert.assertNull(facebookUser.about)
        Assert.assertNull(facebookUser.gender)
        Assert.assertNull(facebookUser.name)
        Assert.assertNull(facebookUser.email)
    }

    @Test
    fun checkEquals() {

        val facebookUser = FacebookUser(facebookID = "123456789")
        val facebookUser1 = FacebookUser(facebookID = "123456789")
        val facebookUser2 = FacebookUser(facebookID = "12345")

        Assert.assertEquals(facebookUser, facebookUser1)
        Assert.assertNotEquals(facebookUser, facebookUser2)
        Assert.assertNotEquals(facebookUser1, facebookUser2)
    }

    @Test
    fun checkHashcode() {
        val facebookUser = FacebookUser(facebookID = "123456789")
        Assert.assertEquals(facebookUser.hashCode(), "123456789".hashCode())
    }

    @Test
    fun checkEqualsHashcode() {
        val facebookUser = FacebookUser(facebookID = "123456789")
        val facebookUser1 = FacebookUser(facebookID = "123456789")

        Assert.assertEquals(facebookUser, facebookUser1)
        Assert.assertEquals(facebookUser.hashCode(), facebookUser1.hashCode())
    }

}