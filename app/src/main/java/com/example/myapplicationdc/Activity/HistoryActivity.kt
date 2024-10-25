package com.example.myapplicationdc.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Adapter.AppointmentAdapter
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityHistoryBinding
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var appointmentAdapter: AppointmentAdapter
    var patientId: Int? = null
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientId = intent.getIntExtra("patientId", -1) // Default value in case patientId is missing
        database = FirebaseDatabase.getInstance().getReference("appointment")

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        // Initialize RecyclerView
        appointmentAdapter = AppointmentAdapter(emptyList()) { appointment, position ->
            showDeleteConfirmationDialog(appointment, position)
        }
        binding.viewHistoryList.layoutManager = LinearLayoutManager(this)
        binding.viewHistoryList.adapter = appointmentAdapter

        // Load appointments from Firebase
        initHistory()
    }

    private fun initHistory() {
        viewModel.history.observe(this) { history ->
            if (history != null && history.isNotEmpty()) {
                appointmentAdapter.updateData(history)
                binding.noHistoryTextView.visibility = View.GONE
                binding.viewHistoryList.visibility = View.VISIBLE
            } else {
                Log.d("HistoryActivity", "No appointments data available.")
                binding.noHistoryTextView.visibility = View.VISIBLE // Show no data message
                binding.viewHistoryList.visibility = View.GONE // Hide the RecyclerView if there's no data
            }
        }

        // Load history from ViewModel
        viewModel.loadHistory(patientId!!)
    }

    private fun showDeleteConfirmationDialog(appointment: Appointment, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Are you sure you want to cancel this reservation?")
            .setPositiveButton("Yes") { _, _ ->
                deleteAppointment(appointment, position)
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteAppointment(appointment: Appointment, position: Int) {
        // Step 1: Remove from Firebase
        database.child(appointment.patientId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (child in dataSnapshot.children) {
                        val appointmentData = child.getValue(Appointment::class.java)
                        if (appointmentData?.appointmentId == appointment.appointmentId) {
                            child.ref.removeValue()
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        updateStatus(appointment.appointmentDate.toString(), "available", appointment.doctorId!!)
                                        // Step 2: Reload the data from Firebase after successful deletion
                                        viewModel.loadHistory(patientId!!)

                                        Toast.makeText(this@HistoryActivity, "Reservation canceled.", Toast.LENGTH_SHORT).show()

                                        // Step 3: Update UI if no items are left
                                        if (appointmentAdapter.itemCount == 0) {
                                            binding.noHistoryTextView.visibility = View.VISIBLE
                                            binding.viewHistoryList.visibility = View.GONE
                                        }
                                    } else {
                                        Toast.makeText(this@HistoryActivity, "Failed to cancel reservation. Please try again.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HistoryActivity", "Database Error: ${error.message}")
                }
            })
    }


    private fun updateStatus(day: String, new_state: String, doctorId: Int) {
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
                                            Toast.makeText(this@HistoryActivity, "Appointment status updated for $day", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@HistoryActivity, "Failed to update status", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        }
                    } else {
                        Toast.makeText(this@HistoryActivity, "Selected day not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HistoryActivity", "Database Error: ${error.message}")
                }
            })
    }
}
