package com.example.myapplicationdc.Activity.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.DoctorViewModel
import com.example.myapplicationdc.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Get the shared DoctorViewModel instance
    private val doctorViewModel: DoctorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize views
        val doctorNameTextView: TextView = binding.doctorname
        val doctorImageView: ImageView = binding.welcomeImage

        // Observe doctorId from the ViewModel and fetch doctor data
        doctorViewModel.doctorId.observe(viewLifecycleOwner) { doctorId ->
            if (!doctorId.isNullOrEmpty()) {
                // Fetch doctor data from Firebase using the doctorId
                fetchDoctorData(doctorId, doctorNameTextView, doctorImageView)
            } else {
                Log.e("HomeFragment", "Doctor ID is null or empty")
            }
        }

        return root
    }

    // Function to fetch doctor data from Firebase
    private fun fetchDoctorData(doctorId: String, nameTextView: TextView, imageView: ImageView) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Doctors").child(doctorId)

        // Attach a listener to read the data at our doctor reference
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctorName = dataSnapshot.child("Name").getValue(String::class.java)
                val doctorImageUrl = dataSnapshot.child("Picture").getValue(String::class.java)

                // Update the TextView with the doctor's name
                nameTextView.text = doctorName ?: "Unknown Doctor" // Handle null case

                // Load the doctor's image using Glide
                Glide.with(this@HomeFragment)
                    .load(doctorImageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground) // Add a placeholder image
                    .error(R.drawable.ic_launcher_background) // Add an error image
                    .into(imageView) // Pass the ImageView instance here
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.e("HomeFragment", "Error fetching doctor data: ${databaseError.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
