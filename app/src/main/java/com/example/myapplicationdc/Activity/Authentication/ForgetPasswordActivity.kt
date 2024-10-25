package com.example.myapplicationdc.Activity.Authentication


import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplicationdc.Activity.BaseActivity
import com.example.myapplicationdc.databinding.ActivityForgetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnForgotPasswordSubmit.setOnClickListener { resetPassword() }
    }

    private fun resetPassword() {
        val email = binding.etForgotPasswordEmail.text.toString().trim()

        if (email.isEmpty()) {
            binding.etForgotPasswordEmail.error = "Email is required"
            return
        }

        showProgressBar()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                hideProgressBar()
                if (task.isSuccessful) {
                    binding.tvSubmitMsg.visibility = View.VISIBLE
                    binding.tvSubmitMsg.text = "An email has been sent to $email to reset your password"

                    Toast.makeText(this,"Password reset email sent",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Failed to send reset email: ${task.exception?.message}",Toast.LENGTH_LONG).show()
                }
            }
    }

}
