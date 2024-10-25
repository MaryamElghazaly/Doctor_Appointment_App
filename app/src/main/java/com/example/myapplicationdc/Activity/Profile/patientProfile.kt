package com.example.myapplicationdc.Activity.Profile

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationdc.Domain.PatientModel
import com.example.myapplicationdc.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*

class patientProfile : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var patientModel: PatientModel

    private lateinit var editPatientName: EditText
    private lateinit var editPatientAge: EditText
    private lateinit var editMedicalHistory: EditText
    private lateinit var editAddress: EditText
    private lateinit var editMobile: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var textViewProfileTitle: TextView // TextView for "Patient Profile"
    private lateinit var btnSavePatient: MaterialButton
    private lateinit var backbtn: ImageView

    private var newPatientId: Int = 0 // New ID to be created if patient not found
    private var existingPatientId: Int = 0 // ID received from the main activity
    private var userEmail: String? = null // Will hold the email passed from SignInActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_profile)

        // Initialize views
        textViewProfileTitle = findViewById(R.id.textViewProfileTitle)
        editPatientName = findViewById(R.id.editPatientName)
        editPatientAge = findViewById(R.id.editPatientAge)
        editMedicalHistory = findViewById(R.id.editMedicalHistory)
        editAddress = findViewById(R.id.edit_pationt_address)
        editMobile = findViewById(R.id.edit_pationt_Mobile)
        spinnerGender = findViewById(R.id.spinnerGender)
        btnSavePatient = findViewById(R.id.btnSavePatient)
        backbtn = findViewById(R.id.btnBack)

        // Set up the gender spinner
        val genderOptions = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        // Get the email passed from SignInActivity
        userEmail = intent.getStringExtra("userEmail")

        // Get the existing patient id from the main activity
        existingPatientId = intent.getIntExtra("patientId", 0)

        // Initialize Firebase reference
        database = FirebaseDatabase.getInstance().getReference("Patients")

        // Save button functionality
        btnSavePatient.setOnClickListener {
            showSaveConfirmationDialog()
        }

        backbtn.setOnClickListener {
            onBackPressed()
            finish()
        }

        // Fetch the number of existing patients to assign a new ID
        fetchPatientCount()

        // Fetch the existing patient data if ID is provided
        if (existingPatientId != 0) {
            fetchPatientData()
        } else {
            // If no existing ID, proceed to create a new patient if needed
            fetchPatientByEmail()
        }
    }

    private fun fetchPatientData() {
        database.child(existingPatientId.toString()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    patientModel = snapshot.getValue(PatientModel::class.java)!!
                    // Set data to views
                    editPatientName.setText(patientModel.pname)
                    editPatientAge.setText(patientModel.age.toString())
                    editMedicalHistory.setText(patientModel.medicalHistory)
                    editAddress.setText(patientModel.patient_address)
                    editMobile.setText(patientModel.patient_Mobile.toString())

                    // Set the spinner value for gender
                    val genderPosition = (spinnerGender.adapter as ArrayAdapter<String>).getPosition(patientModel.gender)
                    spinnerGender.setSelection(genderPosition)

                    // Set the title text to display the patient's name
                    textViewProfileTitle.text = "${patientModel.pname}'s Profile"
                } else {
                    Toast.makeText(this@patientProfile, "Patient not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@patientProfile, "Failed to load patient data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchPatientByEmail() {
        database.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (patientSnapshot in snapshot.children) {
                        val patient = patientSnapshot.getValue(PatientModel::class.java)
                        if (patient != null) {
                            // If patient exists, load their data
                            existingPatientId = patient.id
                            fetchPatientData()
                            return
                        }
                    }
                } else {
                    // No existing patient found, assign new ID for new patient
                    fetchPatientCount()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PatientProfile", "Error fetching patient by email", error.toException())
            }
        })
    }

    private fun fetchPatientCount() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newPatientId = snapshot.childrenCount.toInt() + 1  // Assigning a new ID
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("PatientProfile", "Error fetching patient count", error.toException())
            }
        })
    }

    private fun showSaveConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Update")
            .setMessage("Are you sure you want to save the patient's information?")
            .setPositiveButton("Yes") { dialog, _ ->
                // Check if we have an existing patient or need to create a new one
                if (existingPatientId != 0) {
                    updatePatientData(existingPatientId) // Update existing patient
                } else {
                    createNewPatient() // Create a new patient
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun createNewPatient() {
        // Prepare updated patient model with new data
        val newPatient = PatientModel(
            id = newPatientId, // This will be auto-generated
            pname = editPatientName.text.toString(),
            age = editPatientAge.text.toString().toIntOrNull() ?: 0,
            gender = spinnerGender.selectedItem.toString(),
            patient_address = editAddress.text.toString(),
            patient_Mobile = editMobile.text.toString().toIntOrNull() ?: 0,
            medicalHistory = editMedicalHistory.text.toString(),
            email = userEmail.toString() // Add email to the model
        )

        // Create new patient node in Firebase
        database.child(newPatientId.toString()).setValue(newPatient)
            .addOnSuccessListener {
                Toast.makeText(this, "Patient profile created successfully", Toast.LENGTH_SHORT).show()
                finish() // Optionally navigate to the main activity or perform further actions
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to create patient profile: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("CreatePatient", "Error creating patient profile", error)
            }
    }

    private fun updatePatientData(patientId: Int) {
        // Prepare updated patient model with new data
        val updatedPatient = PatientModel(
            id = patientId, // This is the existing ID
            pname = editPatientName.text.toString(),
            age = editPatientAge.text.toString().toIntOrNull() ?: 0,
            gender = spinnerGender.selectedItem.toString(),
            patient_address = editAddress.text.toString(),
            patient_Mobile = editMobile.text.toString().toIntOrNull() ?: 0,
            medicalHistory = editMedicalHistory.text.toString(),
            email = userEmail.toString() // Keep email the same
        )

        // Update existing patient node in Firebase
        database.child(patientId.toString()).setValue(updatedPatient)
            .addOnSuccessListener {
                Toast.makeText(this, "Patient profile updated successfully", Toast.LENGTH_SHORT).show()
                finish() // Optionally navigate to the main activity or perform further actions
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, "Failed to update patient profile: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdatePatient", "Error updating patient profile", error)
            }
    }
}
