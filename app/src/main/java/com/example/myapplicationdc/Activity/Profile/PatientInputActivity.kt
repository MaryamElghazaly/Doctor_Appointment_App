//package com.example.myapplicationdc.Activity.Profile
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.View
//import android.widget.ArrayAdapter
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.myapplicationdc.Domain.PatientModel
//import com.example.myapplicationdc.R
//import com.example.myapplicationdc.databinding.ActivityPatientInputBinding
//
//class PatientInputActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityPatientInputBinding // ربط الواجهة
//    private lateinit var imageUri: Uri // لتخزين رابط الصورة
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityPatientInputBinding.inflate(layoutInflater) // ربط الواجهة
//        setContentView(binding.root)
//
//        // إعداد الـ Spinner لاختيار الجنس
//        val genders = arrayOf("Select Gender", "Male", "Female")
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
//        binding.spinnerGender.adapter = adapter
//
//        // زر لتحميل صورة الوصفة
//        binding.btnUploadImage.setOnClickListener {
//            openImagePicker() // استدعاء دالة اختيار الصورة
//        }
//
//        // زر حفظ بيانات المريض
//        binding.btnSavePatient.setOnClickListener {
//            savePatientData()
//        }
//    }
//
//    private fun openImagePicker() {
//        val intent = Intent(Intent.ACTION_PICK).apply {
//            type = "image/*"
//        }
//        startActivityForResult(intent, IMAGE_PICK_CODE)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
//            imageUri = data?.data!! // حفظ رابط الصورة المختارة
//            binding.imageViewPrescription.apply {
//                setImageURI(imageUri)
//                visibility = View.VISIBLE // إظهار الصورة في الـ ImageView
//            }
//        }
//    }
//
//    private fun savePatientData() {
//        val patient = PatientModel().apply {
//            pname = binding.editPatientName.text.toString()
//            age = binding.editPatientAge.text.toString().toInt()
//            gender = binding.spinnerGender.selectedItem.toString()
//            medicalHistory = binding.editMedicalHistory.text.toString()
//            prescriptionPictures = imageUri.toString() // تحويل URI إلى String
//        }
//        addPatient(patient)
//    }
//
//    companion object {
//        private const val IMAGE_PICK_CODE = 1000
//    }
//}
