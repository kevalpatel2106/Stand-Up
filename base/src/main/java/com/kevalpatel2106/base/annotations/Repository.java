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
 * This annotation indicates the repository classes in the app architecture.
 * <p>
 * Repository modules are responsible for handling data operations. They provide a clean API to the
 * rest of the app. They know where to get the data from and what API calls to make when data is updated.
 * You can consider them as mediators between different data sources (persistent model, web service, cache, etc.).
 * <p>
 * These classes are usable to provide the data from different sources underlying the layer to the
 * UI layers ({@link UIController}). These will convert the  data revived from webservices or the
 * content provider into the {@link ViewModel} classes and those view model will update the {@link UIController}.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 * @see <a href="https://developer.android.com/topic/libraries/architecture/images/final-architecture.png">Architecture Diagram</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Repository {
}
