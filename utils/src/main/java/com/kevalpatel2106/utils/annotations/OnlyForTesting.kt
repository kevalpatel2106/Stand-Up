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

/**
 * Created by Keval on 25/01/18.
 * Any method, class, property or parameter marked with this annotation indicates that that particular
 * method, class, property or parameter is only for testing purpose. Production code should not be
 * using that method, class, property or parameter.
 *
 * @author "https://github.com/kevalpatel2106"
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
annotation class OnlyForTesting
