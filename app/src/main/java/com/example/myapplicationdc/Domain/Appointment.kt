package com.example.myapplicationdc.Domain

import android.os.Parcel
import android.os.Parcelable

// Define the Appointment class representing each appointment
data class Appointment(
    val appointmentId: Int? = null, // New property for appointment ID
    val appointmentDate: String? = null,
    var doctorId: Int? = null,
    val doctorImage: String? = null,
    val doctorName: String? = null,
    val location: String? = null,
    val patientId: Int? = null
) : Parcelable {

    // No-argument constructor is implicitly provided by default parameter values
    constructor() : this(null, null, null, null, null, null, null)

    // Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readInt(), // Read appointmentId
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        if (parcel.readInt() == 0) null else parcel.readInt() // Handle patientId as nullable
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        if (appointmentId != null) {
            parcel.writeInt(appointmentId)
        } // Write appointmentId
        parcel.writeString(appointmentDate)
        parcel.writeValue(doctorId)
        parcel.writeString(doctorImage)
        parcel.writeString(doctorName)
        parcel.writeString(location)
        if (patientId != null) {
            parcel.writeInt(patientId)
        } else {
            parcel.writeInt(0) // Use 0 to indicate null
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Appointment> {
        override fun createFromParcel(parcel: Parcel): Appointment {
            return Appointment(parcel)
        }

        override fun newArray(size: Int): Array<Appointment?> {
            return arrayOfNulls(size)
        }
    }
}
