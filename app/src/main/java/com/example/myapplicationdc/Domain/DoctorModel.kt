package com.example.myapplicationdc.Domain

import android.os.Parcel
import android.os.Parcelable

// Define the DayModel class representing each day
data class DoctorModel(
    val Address: String = "",
    val Biography: String = "",
    val Id: Int = 0,
    val Name: String = "",
    val Picture: String = "",
    val Special: String = "",
    val Experience: Int = 0,
    val Location: String = "",
    val Mobile: String = "",
    val Patients: String = "",
    val Rating: Double = 0.0,
    val Site: String = "",
    val fees: String = "", // Add fees as a String
    val days: List<DayModel> = listOf() // Add list of days
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "", // Read fees from parcel
        parcel.createTypedArrayList(DayModel.CREATOR) ?: listOf() // Read list of days
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Address)
        parcel.writeString(Biography)
        parcel.writeInt(Id)
        parcel.writeString(Name)
        parcel.writeString(Picture)
        parcel.writeString(Special)
        parcel.writeInt(Experience)
        parcel.writeString(Location)
        parcel.writeString(Mobile)
        parcel.writeString(Patients)
        parcel.writeDouble(Rating)
        parcel.writeString(Site)
        parcel.writeString(fees) // Write fees to parcel
        parcel.writeTypedList(days) // Write list of days
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DoctorModel> {
        override fun createFromParcel(parcel: Parcel): DoctorModel {
            return DoctorModel(parcel)
        }

        override fun newArray(size: Int): Array<DoctorModel?> {
            return arrayOfNulls(size)
        }
    }
}
