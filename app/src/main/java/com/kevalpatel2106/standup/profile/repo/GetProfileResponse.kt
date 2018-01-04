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

package com.kevalpatel2106.standup.profile.repo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.kevalpatel2106.standup.constants.AppConfig
import com.kevalpatel2106.utils.toFloatSafe

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class GetProfileResponse(

        @SerializedName("uid")
        val userId: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("photo")
        val photo: String?,

        @SerializedName("height")
        val height: String,

        @SerializedName("weight")
        val weight: String,

        @SerializedName("gender")
        var gender: String = AppConfig.GENDER_MALE,

        @SerializedName("isVerified")
        val isVerified: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    fun heightFloat() = height.toFloatSafe()

    fun weightFloat() = weight.toFloatSafe()

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(userId)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(photo)
        parcel.writeString(height)
        parcel.writeString(weight)
        parcel.writeString(gender)
        parcel.writeByte(if (isVerified) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GetProfileResponse> {
        override fun createFromParcel(parcel: Parcel): GetProfileResponse {
            return GetProfileResponse(parcel)
        }

        override fun newArray(size: Int): Array<GetProfileResponse?> {
            return arrayOfNulls(size)
        }
    }
}