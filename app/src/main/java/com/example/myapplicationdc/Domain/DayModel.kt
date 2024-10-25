package com.example.myapplicationdc.Domain
import android.os.Parcel
import android.os.Parcelable

data class DayModel(
    val day: String = "",
    var status: String = "" // Keep status as String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "", // Use Elvis operator to avoid null
        parcel.readString() ?: "" // Read status as a String
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(day)
        parcel.writeString(status) // Write status as a String
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DayModel> {
        override fun createFromParcel(parcel: Parcel): DayModel {
            return DayModel(parcel)
        }

        override fun newArray(size: Int): Array<DayModel?> {
            return arrayOfNulls(size)
        }
    }
}