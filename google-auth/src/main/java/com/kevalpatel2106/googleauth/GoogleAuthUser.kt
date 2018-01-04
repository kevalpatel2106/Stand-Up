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

package com.kevalpatel2106.googleauth

import android.net.Uri

/**
 * Created by multidots on 6/21/2016.
 */
data class GoogleAuthUser(var id: String) {
    var idToken: String? = null
    lateinit var name: String
    lateinit var email: String
    var familyName: String? = null
    var photoUrl: Uri? = null

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        other?.let {
            return (other is GoogleAuthUser && other.id == id)
        }
        return false
    }
}
