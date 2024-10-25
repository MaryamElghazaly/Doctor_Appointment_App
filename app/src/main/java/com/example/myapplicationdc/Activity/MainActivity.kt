package com.example.myapplicationdc.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationdc.Activity.Profile.patientProfile
import com.example.myapplicationdc.Adapter.CategoryAdapter
import com.example.myapplicationdc.Adapters.TopDoctorAdapter
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.databinding.ActivityMainBinding
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel = MainViewModel()
    private var patientId: Int? = 0
    private var patientName: String? = null
    private lateinit var topDoctorAdapter: TopDoctorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve patientId and patientName from SignInActivity
        patientId = intent.getIntExtra("patientId", 5)
        patientName = intent.getStringExtra("PATIENT_NAME")
        binding.textView2.text = "Hi $patientName"

        initCategory()
        initTopDoctor()

        // Navigation logic
        binding.doctorlistText.setOnClickListener {
            val intent = Intent(this@MainActivity, TopDoctorActivity::class.java)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }

        binding.history.setOnClickListener {
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }

        binding.favv.setOnClickListener {
            val intent = Intent(this@MainActivity, FavouriteActivity::class.java)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }
        binding.account.setOnClickListener {
            val intent = Intent(this@MainActivity, patientProfile::class.java)
            intent.putExtra("patientId", patientId)
            startActivity(intent)
        }

        setupSearch()
    }

    // Function to load doctors based on the selected category
    private fun loadDoctorsByCategory(selectedCategory: String) {
        binding.progressBarTopDoctors.visibility = View.VISIBLE

        val doctorsRef = FirebaseDatabase.getInstance().getReference("Doctors")
        doctorsRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doctorList = mutableListOf<DoctorModel>()

                for (snapshot in task.result.children) {
                    val doctor = snapshot.getValue(DoctorModel::class.java)
                    doctor?.let {
                        if (doctor.Special == selectedCategory) {
                            doctorList.add(doctor)
                        }
                    }
                }

                topDoctorAdapter.updateDoctors(doctorList)
                binding.progressBarTopDoctors.visibility = View.GONE
            } else {
                Log.e("MainActivity", "Error fetching doctors: ", task.exception)
                binding.progressBarTopDoctors.visibility = View.GONE
            }
        }
    }

    private fun setupSearch() {
        binding.editTextText2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString().lowercase(Locale.ROOT)
                filterDoctors(searchText)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun filterDoctors(searchText: String) {
        try {
            val filteredDoctors = viewModel.doctor.value?.filter {
                it.Name.lowercase(Locale.ROOT).contains(searchText) ||
                        it.Special.lowercase(Locale.ROOT).contains(searchText)
            } ?: emptyList()

            topDoctorAdapter.updateDoctors(filteredDoctors)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error filtering doctors", e)
        }
    }

    private fun initTopDoctor() {
        binding.progressBarTopDoctors.visibility = View.VISIBLE

        viewModel.doctor.observe(this@MainActivity, Observer { doctors ->
            topDoctorAdapter = TopDoctorAdapter(doctors, patientId)
            binding.recyclerViewTopDoctors.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.recyclerViewTopDoctors.adapter = topDoctorAdapter
            binding.progressBarTopDoctors.visibility = View.GONE
        })

        viewModel.loadDoctors()
    }

    private fun initCategory() {
        binding.progressBarCategory.visibility = View.VISIBLE

        viewModel.category.observe(this@MainActivity, Observer { categories ->
            binding.viewCategory.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.viewCategory.adapter = CategoryAdapter(categories) { selectedCategory ->
                loadDoctorsByCategory(selectedCategory)
            }
            binding.progressBarCategory.visibility = View.GONE
        })

        viewModel.loadCategory()
    }
}