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

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.FileReader
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Kevalpatel2106 on 16-Nov-17.
 *
 * @author 'https://github.com/kevalpatel2106'
 */
@RunWith(AndroidJUnit4::class)
class FacebookHelperTest {
    @Test
    fun checkParseFbUser() {
        try {
            val response = JSONObject(FileReader.getStringFromRawFile(InstrumentationRegistry.getInstrumentation().context, com.kevalpatel2106.facebookauth.test.R.raw.fb_response))

            val fbUser = FacebookHelper.parseResponse(response)
            Assert.assertEquals(fbUser.facebookID, "398475894375893")
            Assert.assertEquals(fbUser.name, "John Doe")
            Assert.assertEquals(fbUser.email, "john@example.com")
            Assert.assertEquals(fbUser.gender, "female")
            Assert.assertEquals(fbUser.profilePic, "http://graph.facebook.com/398475894375893/picture?type=normal")
            Assert.assertEquals(fbUser.coverPicUrl, "https://scontent.xx.fbcdn.net/v/t1.0-1/c154.33.413.413/s50x50/427144_108142642648116_878107670_n.jpg?oh=a0dd9353b55c083bab97c5a46dd4ce04&oe=5A94F71A")
            Assert.assertEquals(fbUser.about, "This is the test about.")
            Assert.assertEquals(fbUser.bio, "This is the test bio.")
        } catch (e: JSONException) {
            Assert.fail(e.message)
        }

    }

}