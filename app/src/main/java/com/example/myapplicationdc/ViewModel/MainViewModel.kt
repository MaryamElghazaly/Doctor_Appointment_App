package com.example.myapplicationdc.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.Domain.CategoryModel
import com.example.myapplicationdc.Domain.DoctorModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

class MainViewModel : ViewModel() {

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _doctor = MutableLiveData<MutableList<DoctorModel>>()
    private val _history = MutableLiveData<MutableList<Appointment>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val doctor: LiveData<MutableList<DoctorModel>> = _doctor
    val history: LiveData<MutableList<Appointment>> = _history

    // Method to load categories
    fun loadCategory() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching categories: ${error.message}")
            }
        })
    }

    // Method to load doctors
    fun loadDoctors() {
        val ref = firebaseDatabase.getReference("Doctors")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<DoctorModel>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(DoctorModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _doctor.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching doctors: ${error.message}")
            }
        })
    }

    // Method to load appointment history for a specific patient
    fun loadHistory(patientId: Int) {
        val ref = firebaseDatabase.getReference("appointment")
        ref.child(patientId.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<Appointment>()
                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    if (appointment != null) {
                        lists.add(appointment)
                    }
                }
                _history.value = lists
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching appointments: ${error.message}")
            }
        })
    }

    // Method to add a doctor to favorites for a specific patient
    fun addToFavorites(patientId: Int, doctor: DoctorModel) {
        val patientRef = firebaseDatabase.getReference("Patients").child(patientId.toString()).child("favorites")
        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favoriteDoctors = mutableListOf<DoctorModel>()

                // Check if the favorites list exists and populate it
                if (snapshot.exists()) {
                    snapshot.children.forEach { childSnapshot ->
                        val favoriteDoctor = childSnapshot.getValue(DoctorModel::class.java)
                        if (favoriteDoctor != null) {
                            favoriteDoctors.add(favoriteDoctor)
                        }
                    }
                }

                // Check if the doctor is already in favorites
                if (!favoriteDoctors.contains(doctor)) {
                    favoriteDoctors.add(doctor)
                    patientRef.setValue(favoriteDoctors) // Update the entire favorites list
                        .addOnSuccessListener {
                            Log.d("FirebaseSuccess", "Added doctor to favorites")
                        }
                        .addOnFailureListener {
                            Log.e("FirebaseError", "Failed to add doctor to favorites")
                        }
                } else {
                    Log.d("FirebaseInfo", "Doctor is already in favorites")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching patient data: ${error.message}")
            }
        })
    }
}
