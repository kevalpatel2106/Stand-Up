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

package com.kevalpatel2106.base.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Keval on 10/10/17.
 * This annotation indicates the view model class of the code.
 * <p>
 * A ViewModel provides the data for a specific UI component, such as a fragment or activity, and
 * handles the communication with the business part of data handling, such as calling other components
 * to load the data or forwarding user modifications. The ViewModel does not know about the View and
 * is not affected by configuration changes such as recreating an activity due to rotation.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see <a href="https://developer.android.com/topic/libraries/architecture/images/final-architecture.png">Architecture Diagram</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface ViewModel {
    /**
     * Name of the activity/fragment for which this acts as view mode. This is required field.
     */
    Class value();
}
