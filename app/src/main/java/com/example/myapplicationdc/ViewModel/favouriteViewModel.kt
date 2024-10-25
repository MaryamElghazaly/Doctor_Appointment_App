package com.example.myapplicationdc.ViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.myapplicationdc.Domain.DoctorModel
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

class favouriteViewModel : ViewModel() {

    // Load favorite doctors from SharedPreferences
    fun loadFavoriteDoctors(context: Context, patientId: Int): List<DoctorModel> {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favoriteDoctorsJson = sharedPreferences.getString("favorites_$patientId", null)

        return if (!favoriteDoctorsJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<DoctorModel>>() {}.type
            Gson().fromJson(favoriteDoctorsJson, type)
        } else {
            emptyList()
        }
    }

    // Save favorite doctors to SharedPreferences
    fun saveFavoriteDoctors(context: Context, patientId: Int, favoriteDoctors: List<DoctorModel>) {
        val sharedPreferences = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val favoriteDoctorsJson = Gson().toJson(favoriteDoctors)
        editor.putString("favorites_$patientId", favoriteDoctorsJson)
        editor.apply()
    }

    // Add a doctor to the favorite list
    fun addDoctorToFavorites(context: Context, patientId: Int, doctor: DoctorModel): List<DoctorModel> {
        val favoriteDoctors = loadFavoriteDoctors(context, patientId).toMutableList()
        if (!favoriteDoctors.contains(doctor)) {
            favoriteDoctors.add(doctor)
            saveFavoriteDoctors(context, patientId, favoriteDoctors)
        }
        return favoriteDoctors
    }

    // Remove a doctor from the favorite list
    fun removeDoctorFromFavorites(context: Context, patientId: Int, doctor: DoctorModel): List<DoctorModel> {
        val favoriteDoctors = loadFavoriteDoctors(context, patientId).toMutableList()
        if (favoriteDoctors.contains(doctor)) {
            favoriteDoctors.remove(doctor)
            saveFavoriteDoctors(context, patientId, favoriteDoctors)
        }
        return favoriteDoctors
    }
}
