package com.example.myapplicationdc.ViewModel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DoctorViewModel : ViewModel() {
    private val _doctorId = MutableLiveData<String?>()
    val doctorId: MutableLiveData<String?> get() = _doctorId

    fun setDoctorId(id: String?) {
        _doctorId.value = id
    }
}
