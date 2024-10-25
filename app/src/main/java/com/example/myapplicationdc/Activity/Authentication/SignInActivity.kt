package com.example.myapplicationdc.Activity.Authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.Activity.Doctor_Dashboard
import com.example.myapplicationdc.Activity.MainActivity
import com.example.myapplicationdc.Activity.Profile.patientProfile
import com.example.myapplicationdc.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SignInActivity : BaseActivity() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("userType") ?: "unknown"

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Set up click listeners
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnSignIn.setOnClickListener {
            userLogin()
        }
    }

    // User login with email and password
    private fun userLogin() {
        val email = binding.etSinInEmail.text.toString()
        val password = binding.etSinInPassword.text.toString()

        if (validateForm(email, password)) {
            showProgressBar()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressBar()
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            val userId = currentUser .uid
                            val userEmail = currentUser.email ?: email
                            val user = User(id = userId, email = userEmail, usertype = userType)

                            // Check user type and handle accordingly
                            if (userType.equals("Doctor", ignoreCase = true)) {
                                fetchDoctorIdAndNavigate(userEmail)                            } else {
                                // Check if patient exists in Firebase
                                checkPatientExistsAndNavigate(userId, email)
                            }
                        }
                    } else {
                        Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    private fun fetchDoctorIdAndNavigate(email: String) {
        val doctorRef = database.child("Doctors")

        // Query to match the email in the "Doctors" reference
        val query = doctorRef.orderByChild("email").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (doctorSnapshot in snapshot.children) {
                        val doctorId = doctorSnapshot.key // Fetch the doctor's ID
                        if (doctorId != null) {
                            // Navigate to Doctor Dashboard with the doctor ID
                            Log.d("FetchDoctor", "Fetched ID: $doctorId")
                            navigateToDoctorDashboard(doctorId, email)
                        }
                    }
                } else {
                    Toast.makeText(this@SignInActivity, "Doctor not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SignInActivity", "Database error: ${error.message}")
                Toast.makeText(this@SignInActivity, "Failed to fetch doctor data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Check if patient exists and navigate accordingly

    private fun checkPatientExistsAndNavigate(userId: String, email: String) {
        val patientRef = database.child("Patients").orderByChild("email").equalTo(email)
        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Patient exists, fetch details and navigate to MainActivity
                    for (patientSnapshot in snapshot.children) {
                        val patientId = patientSnapshot.child("id").getValue(Int::class.java)
                        val patientName = patientSnapshot.child("pname").getValue(String::class.java)
                        if (patientId != null && patientName != null) {
                            navigateToPatientHome(patientId, patientName, email)
                        }
                    }
                } else {
                    // Patient does not exist, navigate to patient profile without creating a new node
                    navigateToPatientProfile(email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SignInActivity", "Database error: ${error.message}")
                Toast.makeText(this@SignInActivity, "Failed to fetch patient data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Navigate to Patient Profile for new patients
    private fun navigateToPatientProfile(email: String) {
        val intent = Intent(this, patientProfile::class.java)
        intent.putExtra("userEmail", email) // Pass email to profile
        startActivity(intent)
        finish()
    }


    // Navigate to Doctor Dashboard with doctor ID
    private fun navigateToDoctorDashboard(doctorId: String?, email: String) {
        val intent = Intent(this, Doctor_Dashboard::class.java)
        intent.putExtra("doctorId", doctorId) // Pass doctor ID
        intent.putExtra("userEmail", email) // Pass user email
        startActivity(intent)
        finish()
    }

    // Navigate to MainActivity with patient ID
    private fun navigateToPatientHome(patientId: Int, patientName: String, email: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("patientId", patientId)
        intent.putExtra("PATIENT_NAME", patientName) // Pass the patient's name
        intent.putExtra("EMAIL", email)
        startActivity(intent)
        finish() // Optional: finish the current activity if you don't want to return to it
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "Please enter a valid email"
                false
            }
            TextUtils.isEmpty(password) -> {
                binding.tilPassword.error = "Please enter a password"
                false
            }
            else -> true
        }
    }
}