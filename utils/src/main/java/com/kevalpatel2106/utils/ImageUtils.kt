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

package com.kevalpatel2106.utils

import android.content.Context
import android.graphics.Bitmap
import android.support.annotation.WorkerThread
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Kevalpatel2106 on 30-Sep-17.
 * Utils to deal with images and bitmaps.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
object ImageUtils {

    /**
     * Save bitmap image to the cached file. You should call this on background thread.
     *
     * @param context Instance of activity
     * @param bitmap  Bitmap to save
     * @return new file or null if error occurs while saving the file.
     */
    @JvmStatic
    @WorkerThread
    fun saveImageFile(context: Context,
                      bitmap: Bitmap): File? {
        var out: FileOutputStream? = null
        try {
            val file = File(FileUtils.getCacheDir(context).absolutePath
                    + "/"
                    + System.currentTimeMillis()
                    + ".png")
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            return file
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            try {
                if (out != null) out.close()
            } catch (e: IOException) {
                Timber.e(e)
            }
        }
        return null
    }

    /**
     * Resize the bitmap.
     *
     * @param bitmap Original bitmap.
     * @param maxDimension Max allowed dimensions.
     */
    @JvmStatic
    @WorkerThread
    fun resizeImage(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        //Check if any of the dimensions are larger than max dimensions provided.
        return if (Math.max(width, height) > maxDimension) {
            val aspectRatio: Float = width.toFloat() / height.toFloat()
            return if (width > maxDimension) {  //Width is larger than max allowed dimension
                Bitmap.createScaledBitmap(bitmap,
                        maxDimension,
                        (maxDimension / aspectRatio).toInt(),
                        false)
            } else {   //Height is larger than max allowed dimension
                Bitmap.createScaledBitmap(bitmap,
                        (maxDimension * aspectRatio).toInt(),
                        maxDimension,
                        false)
            }
        } else {
            //Return original bitmap.
            bitmap
        }
    }

}