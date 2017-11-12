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

package com.kevalpatel2106.base.annotations

/**
 * Created by Keval on 10/10/17.
 * This annotation indicates the model class of the code.
 *
 *
 * This type of class generally holds the
 * data for the UI components which were either fetched from the server or database/content providers.
 * These classes are either POJO classes in Java or data classes in Kotlin.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 * @see [Architecture Diagram](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Model
