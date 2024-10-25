package com.example.myapplicationdc.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.ActivityDoctorDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Doctor_Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorDashboardBinding
    private val doctorViewModel: DoctorViewModel by viewModels() // Access the ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityDoctorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the Toolbar as the ActionBar
        setSupportActionBar(binding.toolbar)

        // Retrieve the doctorId from the Intent
        val doctorId = intent.getStringExtra("doctorId")
        if (doctorId != null) {
            doctorViewModel.setDoctorId(doctorId) // Set doctorId in ViewModel
        } else {
            Log.e("Doctor_Dashboard", "Doctor ID is null!")
        }

        val navView: BottomNavigationView = binding.navView

        // Set up navigation with the NavController
        val navController = findNavController(R.id.nav_host_fragment_activity_doctor_dashboard)

        if (navController != null) {
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_appointments, R.id.navigation_profile
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)
        } else {
            Log.e("Doctor_Dashboard", "NavController is null. Check your layout and navigation graph.")
        }
    }
}
