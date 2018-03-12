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

/**
 * Created by Keval on 02/03/18.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
object AppVersion {
    /**
     * Version code of the application.
     */
    const val versionCode = 6

    // Prepare the version name.
    // Version name scheme: major.minor.patch

    private const val versionMajor = 0
    private const val versionMinor = 4
    private const val versionPatch = 0

    /**
     * Prepare the version name in [versionMajor].[versionMinor].[versionPatch] format.
     */
    val versionName = "$versionMajor.$versionMinor.$versionPatch"
}