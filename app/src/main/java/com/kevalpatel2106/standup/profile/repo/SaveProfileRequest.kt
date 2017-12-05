package com.kevalpatel2106.standup.profile.repo

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

/**
 * Created by Keval on 28/11/17.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
data class SaveProfileRequest(

        @SerializedName("uid")
        val userId: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("photo")
        var photo: String? = null,

        @SerializedName("height")
        val height: String,

        @SerializedName("weight")
        val weight: String,

        @SerializedName("gender")
        val gender: String
) : Parcelable {
        constructor(parcel: Parcel) : this(
                parcel.readLong(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString(),
                parcel.readString())

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeLong(userId)
                parcel.writeString(name)
                parcel.writeString(email)
                parcel.writeString(photo)
                parcel.writeString(height)
                parcel.writeString(weight)
                parcel.writeString(gender)
        }

        override fun describeContents(): Int {
                return 0
        }

        companion object CREATOR : Parcelable.Creator<SaveProfileRequest> {
                override fun createFromParcel(parcel: Parcel): SaveProfileRequest {
                        return SaveProfileRequest(parcel)
                }

                override fun newArray(size: Int): Array<SaveProfileRequest?> {
                        return arrayOfNulls(size)
                }
        }
}