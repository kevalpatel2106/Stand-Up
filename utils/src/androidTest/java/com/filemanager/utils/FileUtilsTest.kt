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
import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.kevalpatel2106.testutils.BaseTestClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by Keval on 11/10/17.

 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@RunWith(AndroidJUnit4::class)
class FileUtilsTest : BaseTestClass() {
    private lateinit var mContext: Context

    @Before
    fun setUp() {
        mContext = InstrumentationRegistry.getTargetContext()
    }

    override fun getActivity(): Activity? = null

    @Test
    fun getCacheDir() {
        //Check if the output is not null.
        assertNotNull(FileUtils.getCacheDir(mContext))

        val cachePath = FileUtils.getCacheDir(mContext).absolutePath
        if (mContext.externalCacheDir != null)
            assertEquals(mContext.externalCacheDir.absolutePath, cachePath)
        else
            assertEquals(mContext.cacheDir.absolutePath, cachePath)
    }
}