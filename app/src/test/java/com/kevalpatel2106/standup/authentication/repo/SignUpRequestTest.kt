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

package com.kevalpatel2106.standup.authentication.repo

import android.net.Uri
import com.kevalpatel2106.facebookauth.FacebookUser
import com.kevalpatel2106.googleauth.GoogleAuthUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 06-Dec-17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(JUnit4::class)
class SignUpRequestTest {

    @Test
    @Throws(IOException::class)
    fun checkInitWithParams() {
        val signUpRequest = SignUpRequest(
                email = "test@example.com",
                password = "123456789",
                displayName = "Test User",
                photo = "http://google.com")

        assertEquals(signUpRequest.email, "test@example.com")
        assertEquals(signUpRequest.password, "123456789")
        assertEquals(signUpRequest.displayName, "Test User")
        assertEquals(signUpRequest.photo, "http://google.com")
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithFbUser() {
        val facebookUser = FacebookUser(facebookID = "123456789")
        facebookUser.coverPicUrl = "http://coverpic.com"
        facebookUser.profilePic = "http://profilepic.com"
        facebookUser.bio = "This is the test bio"
        facebookUser.about = "This is the test bio"
        facebookUser.gender = "male"
        facebookUser.name = "Test User"
        facebookUser.email = "test@example.com"

        var signUpRequest = SignUpRequest(fbUser = facebookUser)
        assertEquals(signUpRequest.email, "test@example.com")
        assertNull(signUpRequest.password)
        assertEquals(signUpRequest.displayName, "Test User")
        assertEquals(signUpRequest.photo, "http://profilepic.com")

        //With null photo url
        facebookUser.coverPicUrl = null
        facebookUser.profilePic = null
        signUpRequest = SignUpRequest(fbUser = facebookUser)
        assertNull(signUpRequest.photo)
    }

    @Test
    @Throws(IOException::class)
    fun checkInitWithGoogleUser() {
        val googleUser = GoogleAuthUser("123456789")
        googleUser.familyName = "User"
        googleUser.photoUrl = Uri.EMPTY
        googleUser.name = "Test User"
        googleUser.email = "test@example.com"

        var signUpRequest = SignUpRequest(googleUser = googleUser)
        assertEquals(signUpRequest.email, "test@example.com")
        assertNull(signUpRequest.password)
        assertEquals(signUpRequest.displayName, "Test User")
        assertNull(signUpRequest.photo)

        //With null photo url
        googleUser.photoUrl = null
        signUpRequest = SignUpRequest(googleUser = googleUser)
        assertNull(signUpRequest.photo)
    }
}