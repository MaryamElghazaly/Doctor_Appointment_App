package com.example.myapplicationdc.Activity.Authentication

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.tvLoginPage.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val email = binding.etSinUpEmail.text.toString().trim()
        val password = binding.etSinUpPassword.text.toString().trim()

        if (email.isEmpty()) {
            binding.etSinUpEmail.error = "Email is required"
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@gmail.com")) {
            binding.etSinUpEmail.error = "Please enter a valid email address (e.g. example@gmail.com)"
            return
        }

        if (password.isEmpty()) {
            binding.etSinUpPassword.error = "Password is required"
            return
        }

        showProgressBar()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                hideProgressBar()
                if (task.isSuccessful) {
                    showUserTypeDialog(email) // Show dialog for user type selection
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showUserTypeDialog(email: String) {
        val userTypes = arrayOf("Doctor", "Patient")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select User Type")
        builder.setItems(userTypes) { _, which ->
            val selectedType = userTypes[which]
            // Navigate to SignInActivity with user type and email
            val intent = Intent(this, SignInActivity::class.java).apply {
                putExtra("userType", selectedType)
                putExtra("userEmail", email)
            }
            startActivity(intent)
            finish()
        }
        builder.show()
    }
}
