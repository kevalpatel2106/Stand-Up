/*
 *  Copyright 2017 Keval Patel.
 *
 *  Licensed under the GNU General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://www.gnu.org/licenses/gpl-3.0.en.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.filemanager.utils

import android.app.Activity
import android.graphics.Bitmap
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Keval on 15/10/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class ImageUtilsTest : BaseTestClass() {
    override fun getActivity(): Activity? {
        return null
    }

    @Test
    fun resizeImage() {
        //If the image is smaller than max dimen
        var b = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        var resized = ImageUtils.resizeImage(b, 500)
        Assert.assertEquals(b.width, resized.width)
        Assert.assertEquals(b.height, resized.height)

        //1:1 aspect ratio
        b = Bitmap.createBitmap(600, 600, Bitmap.Config.ARGB_4444)
        resized = ImageUtils.resizeImage(b, 500)
        Assert.assertEquals(resized.width, 500)
        Assert.assertEquals(resized.height, 500)

        //1:2 aspect ratio
        b = Bitmap.createBitmap(600, 1200, Bitmap.Config.ARGB_4444)
        resized = ImageUtils.resizeImage(b, 500)
        Assert.assertEquals(resized.width, 500)
        Assert.assertEquals(resized.height, 1000)

        //1:2 aspect ratio
        b = Bitmap.createBitmap(400, 800, Bitmap.Config.ARGB_4444)
        resized = ImageUtils.resizeImage(b, 500)
        Assert.assertEquals(resized.width, 250)
        Assert.assertEquals(resized.height, 500)

        //2:1 aspect ratio
        b = Bitmap.createBitmap(1400, 700, Bitmap.Config.ARGB_4444)
        resized = ImageUtils.resizeImage(b, 500)
        Assert.assertEquals(resized.width, 500)
        Assert.assertEquals(resized.height, 250)
    }

}