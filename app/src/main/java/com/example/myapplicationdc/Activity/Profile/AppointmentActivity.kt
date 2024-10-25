package com.example.myapplicationdc.Activity.Profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Activity.PaymentActivity
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.Domain.DayModel
import com.example.myapplicationdc.databinding.ActivityAppointmentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import kotlin.properties.Delegates

class AppointmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAppointmentBinding
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private var doctorId: Int = 0
    private lateinit var selectedDay: String
    lateinit var appointmentKeys: MutableList<String>
    var location: String? = null
    private var appointmentReserved = false
    private var appointmentId: Int = 1 // Default starting value
    private var patientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppointmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // Get patientId from intent
        patientId = intent.getIntExtra("patientId",-2)

        // Load the appointmentId from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        appointmentId = sharedPreferences.getInt("appointmentId", 1) // Default to 1 if not found

        doctorId = intent.getIntExtra("doctor_id", 0)
        val doctorName = intent.getStringExtra("doctor_name") ?: ""
        val doctorImage = intent.getStringExtra("doctor_image") ?: ""
        location = intent.getStringExtra("Location")

        appointmentKeys = mutableListOf()

        binding.doctorNameEditText.setText(doctorName)
        setDoctorImage(doctorImage)
        database = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId.toString())
        database1 = FirebaseDatabase.getInstance().getReference("appointment")

        fetchAvailableDays()

        binding.reserveButton.setOnClickListener {
            if (::selectedDay.isInitialized) {
                appointmentReserved = true
                updateStatus(selectedDay, "reserved")

                // Disable the reserve button to prevent duplicate reservations
                binding.reserveButton.isEnabled = false
                binding.reserveButton.text = "Reserving..." // Optional: Change button text to indicate processing

                reserveAppointment(doctorName, doctorId, selectedDay, location.toString(), doctorImage)
            } else {
                Toast.makeText(this, "Please select a day", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        if (appointmentReserved) {
            AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Do you want to cancel the reservation?")
                .setPositiveButton("Yes") { _, _ ->
                    cancelReservation()
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialog, _ ->
                    onBackPressedDispatcher.onBackPressed()
                    dialog.dismiss()
                }
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun cancelReservation() {
        updateStatus(selectedDay, "available")

        database1.child(patientId.toString()).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Reservation canceled", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Log.e("tag1", "Failed to cancel reservation: ${it.message}")
        }
    }

    private fun fetchAvailableDays() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId.toString())

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val availableDaysList = mutableListOf<DayModel>()

                val availableDays = snapshot.child("availableDays")
                for (daySnapshot in availableDays.children) {
                    val day = daySnapshot.child("day").value as? String
                    val status = daySnapshot.child("status").value as? String

                    val dayTrimmed = day?.trim('"')
                    val statusTrimmed = status?.trim('"')

                    if (dayTrimmed != null && statusTrimmed != null && statusTrimmed == "available") {
                        availableDaysList.add(DayModel(dayTrimmed, statusTrimmed))
                    }
                }

                setUpAutoCompleteTextView(availableDaysList.map { it.day })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("tag1", "Database Error: ${error.message}")
            }
        })
    }

    private fun setUpAutoCompleteTextView(availableDays: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableDays)
        binding.days.setAdapter(adapter)

        binding.days.setOnClickListener {
            binding.days.showDropDown()
        }

        binding.days.setOnItemClickListener { _, _, position, _ ->
            selectedDay = availableDays[position]
        }
    }

    private fun updateStatus(day: String, new_state: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId.toString())

        databaseReference.child("availableDays")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (daySnapshot in snapshot.children) {
                            val dayValue = daySnapshot.child("day").value as? String
                            if (dayValue != null && dayValue.trim('"') == day) {
                                daySnapshot.ref.child("status").setValue(new_state)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(this@AppointmentActivity, "Appointment reserved for $day", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@AppointmentActivity, "Failed to reserve appointment", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(this@AppointmentActivity, "Selected day not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("tag1", "Database Error: ${error.message}")
                }
            })
    }

    private fun setDoctorImage(imageUrl: String) {
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .into(binding.doctorImageView)
        }
    }

    private fun reserveAppointment(doctorName: String, doctorId: Int, appointmentDate: String, location: String, doctorImage: String) {
        val appointment = Appointment(
            appointmentId = appointmentId, // Use current appointmentId
            appointmentDate = appointmentDate,
            doctorId = doctorId,
            doctorImage = doctorImage,
            doctorName = doctorName,
            location = location,
            patientId = patientId // Use the patientId obtained from the intent
        )

        val ref = database1.child(patientId.toString())
        val appointmentRef = ref.push()

        appointmentRef.setValue(appointment).addOnSuccessListener {
            Toast.makeText(this, "Successfully Reserved", Toast.LENGTH_SHORT).show()

            // Re-enable the button after successful reservation
            appointmentKeys.add(appointmentId.toString()) // Store appointmentId as string
            saveKeysToPreferences(appointmentKeys)

            appointmentId++ // Increment appointmentId after successful reservation

            // Save the updated appointmentId to SharedPreferences
            val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("appointmentId", appointmentId)
            editor.apply()
            finish()

            // TODO: Trigger notification here (use the appointment details if needed)
        }.addOnFailureListener { exception ->
            Log.e("reserveAppointment", "Failed to reserve appointment: ${exception.message}")

            // Re-enable the button after failure
            binding.reserveButton.isEnabled = true
            binding.reserveButton.text = "Reserve" // Reset button text
        }
    }

    private fun saveKeysToPreferences(keys: List<String>) {
        val sharedPreferences = getSharedPreferences("MyAppPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(keys)
        editor.putString("appointmentKeys", json)
        editor.apply()
        Log.d("keys", "$appointmentKeys")
    }
}
