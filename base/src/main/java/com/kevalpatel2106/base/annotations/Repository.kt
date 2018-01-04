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
 * This annotation indicates the repository classes in the app architecture.
 *
 *
 * Repository modules are responsible for handling data operations. They provide a clean API to the
 * rest of the app. They know where to get the data from and what API calls to make when data is updated.
 * You can consider them as mediators between different data sources (persistent model, web service, cache, etc.).
 *
 *
 * These classes are usable to provide the data from different sources underlying the layer to the
 * UI layers ([UIController]). These will convert the  data revived from webservices or the
 * content provider into the [ViewModel] classes and those view model will update the [UIController].
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 * @see [Architecture Diagram](https://developer.android.com/topic/libraries/architecture/images/final-architecture.png)
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class Repository
