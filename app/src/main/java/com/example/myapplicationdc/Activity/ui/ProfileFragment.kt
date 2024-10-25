package com.example.myapplicationdc.Activity.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private lateinit var editDoctorName: EditText
    private lateinit var editSpecialization: EditText
    private lateinit var editExperience: EditText
    private lateinit var editFees: EditText
    private lateinit var editDoctorAddress: EditText
    private lateinit var editDoctorMobile: EditText
    private lateinit var btnSaveDoctor: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var doctorRef: DatabaseReference
    private val doctorViewModel: DoctorViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Observe doctorId and fetch data
        doctorViewModel.doctorId.observe(viewLifecycleOwner) { doctorId ->
            if (!doctorId.isNullOrEmpty()) {
                fetchDoctorData(doctorId)
            } else {
                Log.e("ProfileFragment", "Doctor ID is null or empty")
            }
        }

        setupUI(view)
        return view
    }

    private fun setupUI(view: View) {
        editDoctorName = view.findViewById(R.id.editDoctorName)
        editSpecialization = view.findViewById(R.id.editSpecialization)
        editExperience = view.findViewById(R.id.editExperience)
        editFees = view.findViewById(R.id.editFees)
        editDoctorAddress = view.findViewById(R.id.editDoctorAddress)
        editDoctorMobile = view.findViewById(R.id.editDoctorMobile)
        btnSaveDoctor = view.findViewById(R.id.btnSaveDoctor)
        btnBack = view.findViewById(R.id.btnBack)

        btnBack.setOnClickListener { activity?.onBackPressed() }
        btnSaveDoctor.setOnClickListener { showConfirmationDialog() }
    }

    private fun fetchDoctorData(doctorId: String) {
        doctorRef = FirebaseDatabase.getInstance().getReference("Doctors/$doctorId")
        doctorRef.get().addOnSuccessListener { snapshot ->
            val doctor = snapshot.getValue(DoctorModel::class.java)
            if (doctor != null) {
                populateFields(doctor) // Pass the doctor object
            } else {
                Toast.makeText(context, "Doctor not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch doctor data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateFields(doctor: DoctorModel) {
        editDoctorName.setText(doctor.Name)
        editSpecialization.setText(doctor.Special)
        editExperience.setText(doctor.Experience.toString())
        editFees.setText(doctor.fees)
        editDoctorAddress.setText(doctor.Address)
        editDoctorMobile.setText(doctor.Mobile)
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Update")
            .setMessage("Are you sure you want to update the doctor information?")
            .setPositiveButton("Yes") { _, _ ->
                doctorViewModel.doctorId.value?.let { doctorId ->
                    updateDoctorData(doctorId) // Pass doctorId to updateDoctorData
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateDoctorData(doctorId: String) {
        doctorRef = FirebaseDatabase.getInstance().getReference("Doctors/$doctorId")
        doctorRef.get().addOnSuccessListener { snapshot ->
            val existingDoctor = snapshot.getValue(DoctorModel::class.java)

            if (existingDoctor != null) {
                // Create an updated DoctorModel based on the user input or the existing values
                val updatedDoctor = DoctorModel(
                    Address = editDoctorAddress.text.toString().ifEmpty { existingDoctor.Address },
                    Biography = existingDoctor.Biography, // Keep existing value
                    Id = doctorId.toInt(),
                    Name = editDoctorName.text.toString().ifEmpty { existingDoctor.Name },
                    Picture = existingDoctor.Picture, // Keep existing value
                    Special = editSpecialization.text.toString().ifEmpty { existingDoctor.Special },
                    Experience = editExperience.text.toString().toIntOrNull() ?: existingDoctor.Experience,
                    Location = existingDoctor.Location, // Keep existing value
                    Mobile = editDoctorMobile.text.toString().ifEmpty { existingDoctor.Mobile },
                    Patients = existingDoctor.Patients, // Keep existing value
                    Rating = existingDoctor.Rating, // Keep existing value
                    Site = existingDoctor.Site, // Keep existing value
                    fees = editFees.text.toString().ifEmpty { existingDoctor.fees },
                    days = existingDoctor.days // Keep existing value
                )

                doctorRef.setValue(updatedDoctor).addOnSuccessListener {
                    Toast.makeText(context, "Doctor data updated successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to update doctor data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Doctor not found", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to fetch doctor data", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_DOCTOR_ID = "doctorId"

        fun newInstance(doctorId: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_DOCTOR_ID, doctorId)
                }
                }
      }
}