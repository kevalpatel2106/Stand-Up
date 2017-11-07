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

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Keval on 15/10/17.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
class FileUtilsUnitTest {

    @Test
    fun getHumanReadableSize() {
        assertEquals(FileUtils.humanReadableFileSize(0), "0 B")
        assertEquals(FileUtils.humanReadableFileSize(27), "27 B")
        assertEquals(FileUtils.humanReadableFileSize(1023), "1023 B")
        assertEquals(FileUtils.humanReadableFileSize(1024), "1.0 KB")
        assertEquals(FileUtils.humanReadableFileSize(1728), "1.7 KB")
        assertEquals(FileUtils.humanReadableFileSize(110592), "108.0 KB")
        assertEquals(FileUtils.humanReadableFileSize(7077888), "6.8 MB")
        assertEquals(FileUtils.humanReadableFileSize(452984832), "432.0 MB")
        assertEquals(FileUtils.humanReadableFileSize(28991029248), "27.0 GB")
        assertEquals(FileUtils.humanReadableFileSize(1855425871872), "1.7 TB")
        assertEquals(FileUtils.humanReadableFileSize(9223372036854775807), "8.0 EB")
    }
}