package com.example.myapplicationdc.Activity.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.FragmentNotificationsBinding
import com.google.firebase.database.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the DoctorViewModel
        val doctorViewModel = ViewModelProvider(requireActivity()).get(DoctorViewModel::class.java)

        // Observe the doctorId LiveData
        doctorViewModel.doctorId.observe(viewLifecycleOwner, Observer { doctorId ->
            if (doctorId != null) {
                // Initialize Firebase Database reference
                databaseReference = FirebaseDatabase.getInstance().getReference("appointment")
                fetchAppointments(doctorId) // Fetch appointments for the retrieved doctorId
            }
        })
    }

    // Function to fetch appointments where doctorId matches
    private fun fetchAppointments(doctorId: String) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("NotificationsFragment", "No appointments found for doctorId: $doctorId")
                    return
                }

                binding.tableLayout.removeAllViews() // Clear previous data

                for (appointmentSnapshot in dataSnapshot.children) {
                    val appointmentData = appointmentSnapshot.value as? Map<*, *>
                    val appointment = appointmentData?.let {
                        Appointment(
                            appointmentId = it["appointmentId"] as? Int ?: 0,
                            appointmentDate = it["appointmentDate"] as? String ?: "Unknown",
                            doctorId = it["doctorId"] as? Int ?: 0,
                            patientId = it["patientId"] as? Int ?: 0,
                            doctorName = it["doctorName"] as? String ?: "Unknown",
                            doctorImage = it["doctorImage"] as? String ?: "Unknown",
                            location = it["location"] as? String ?: "Unknown"
                        )
                    } ?: continue

                    Log.d("NotificationsFragment", "Fetched appointment: ${appointment.appointmentDate}, ID: ${appointment.appointmentId}")

                    // Check if the doctorId matches
                    if (appointment.doctorId.toString() == doctorId) {
                        appointment.patientId?.let { patientId ->
                            fetchPatientDetails(patientId) { patientName, medicalHistory ->
                                addRowToTable(patientName, appointment.appointmentDate!!, medicalHistory)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationsFragment", "Error fetching appointments: ${databaseError.message}")
            }
        })
    }

    private fun fetchPatientDetails(patientId: Int, callback: (String, String) -> Unit) {
        val patientReference = FirebaseDatabase.getInstance().getReference("Patients")

        patientReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var found = false
                for (patientSnapshot in dataSnapshot.children) {
                    val id = patientSnapshot.child("id").getValue(Int::class.java) ?: 0

                    if (id == patientId) {
                        found = true
                        val patientName = patientSnapshot.child("pname").getValue(String::class.java) ?: "Unknown"
                        val medicalHistory = patientSnapshot.child("medicalHistory").getValue(String::class.java) ?: "No history"

                        Log.d("NotificationsFragment", "Fetched patientName: $patientName, medicalHistory: $medicalHistory")

                        callback(patientName, medicalHistory)
                        return
                    }
                }

                if (!found) {
                    Log.d("NotificationsFragment", "Patient ID $patientId not found")
                    callback("Unknown", "No history")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("NotificationsFragment", "Error fetching patient details: ${databaseError.message}")
            }
        })
    }

    private fun addRowToTable(patientName: String, appointmentDay: String, medicalHistory: String) {
        val tableLayout: TableLayout = binding.tableLayout
        val row = TableRow(context)

        val nameTextView = TextView(context).apply {
            text = patientName
            setPadding(8, 8, 8, 8)
        }
        row.addView(nameTextView)

        val dayTextView = TextView(context).apply {
            text = appointmentDay
            setPadding(8, 8, 8, 8)
        }
        row.addView(dayTextView)

        val historyTextView = TextView(context).apply {
            text = medicalHistory
            setPadding(8, 8, 8, 8)
        }
        row.addView(historyTextView)

        tableLayout.addView(row)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
