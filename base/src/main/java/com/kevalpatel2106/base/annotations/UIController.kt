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

package com.kevalpatel2106.base.annotations

/**
 * Created by Keval on 10/10/17.
 * This annotation indicates the UI object that displays the data on the screen. (e.g. [android.app.Activity]
 * or [android.app.Fragment]) These classes gets the data from the [ViewModel] and displays
 * them on the screen.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 * @see [Architecture Diagram](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class UIController
