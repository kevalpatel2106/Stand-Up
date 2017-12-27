/*
 *  Copyright 2017 Keval Patel.
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

package com.kevalpatel2106.standup.profile.repo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class GetProfileRequest(

        @SerializedName("uid")
        val userId: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(parcel.readLong())

    override fun equals(other: Any?): Boolean {
        return if (other is GetProfileRequest) {
            other.userId == userId
        } else false
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetProfileRequest> {
        override fun createFromParcel(parcel: Parcel): GetProfileRequest {
            return GetProfileRequest(parcel)
        }

        override fun newArray(size: Int): Array<GetProfileRequest?> {
            return arrayOfNulls(size)
        }
    }
}