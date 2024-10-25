package com.example.myapplicationdc.Activity.Authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplicationdc.Activity.Authentication.SignInActivity
import com.example.myapplicationdc.R
import com.example.myapplicationdc.databinding.ActivityChooseYourDirectionsBinding

class ChooseYourDirectionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseYourDirectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChooseYourDirectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnDoctor.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("userType", "doctor")
            startActivity(intent)
        }

        binding.btnPatient.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            intent.putExtra("userType", "patient")
            startActivity(intent)
        }





    }


}