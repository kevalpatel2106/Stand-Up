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

package com.kevalpatel2106.utils.annotations

import android.support.annotation.LayoutRes

/**
 * Created by Keval on 15/10/17.
 * This annotation indicates that the class is the view holder for the particular row.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class ViewHolder(
        /**
         * Row file layout for the view holder.
         */
        @LayoutRes
        val value: Int)
