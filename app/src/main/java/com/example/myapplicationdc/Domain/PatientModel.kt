package com.example.myapplicationdc.Domain

import android.os.Parcel
import android.os.Parcelable

data class PatientModel(
    var id: Int = 0,
    var pname: String = "",
    var age: Int = 0,
    var gender: String = "",
    var patient_address: String = "",
    var patient_Mobile: Int = 0,
    var medicalHistory: String = "",
    var email: String = ""  // Added email property
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),  // Reading mobile as Int
        parcel.readString().toString(),
        parcel.readString().toString() // Reading email
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(pname)
        parcel.writeInt(age)
        parcel.writeString(gender)
        parcel.writeString(patient_address) // Writing address
        parcel.writeInt(patient_Mobile) // Writing mobile as Int
        parcel.writeString(medicalHistory)
        parcel.writeString(email) // Writing email
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PatientModel> {
        override fun createFromParcel(parcel: Parcel): PatientModel {
            return PatientModel(parcel)
        }

        override fun newArray(size: Int): Array<PatientModel?> {
            return arrayOfNulls(size)
        }
    }
}
