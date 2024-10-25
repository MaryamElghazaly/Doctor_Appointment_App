package com.example.myapplicationdc.Activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Activity.Profile.AppointmentActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.MainViewModel
import com.example.myapplicationdc.ViewModel.favouriteViewModel
import com.example.myapplicationdc.databinding.ActivityDetailBinding
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var item: DoctorModel
    private val favouriteViewModel: favouriteViewModel by viewModels()
    var patientId: Int = 0
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getBundle()
        patientId = intent.getIntExtra("patientId", 6)

        // Load favorite status
        loadFavoriteStatus()
    }

    private fun getBundle() {
        item = intent.getParcelableExtra<DoctorModel>("object")!!

        binding.apply {
            titleTxt.text = item.Name
            specialTxt.text = item.Special
            patiensTxt.text = item.Patients
            bioTxt.text = item.Biography
            addressTxt.text = item.Address
            experienceTxt.text = "${item.Experience} Years"
            ratingTxt.text = "${item.Rating}"

            // Back button functionality
            backBtn.setOnClickListener {
                finish()
            }

            // Website button functionality
            websiteBtn.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(item.Site)
                startActivity(i)
            }

            // Message button functionality
            messageBtn.setOnClickListener {
                val uri = Uri.parse("smsto:${item.Mobile}")
                val intent = Intent(Intent.ACTION_SENDTO, uri)
                intent.putExtra("sms_body", "the SMS text")
                startActivity(intent)
            }

            // Call button functionality
            callBtn.setOnClickListener {
                val uri = "tel:${item.Mobile.trim()}"
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(uri))
                startActivity(intent)
            }

            // Direction button functionality
            directionBtn.setOnClickListener {
                val locationUrl = if (item.Location.trim().isNotEmpty()) {
                    item.Location.trim()
                } else {
                    "https://www.google.com/maps/search/?api=1&query=${item.Address.trim()}"
                }
                Log.d("DetailActivity", "Location URL: $locationUrl")
                val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl)))
                }
            }

            // Share button functionality
            shareBtn.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, item.Name)
                intent.putExtra(Intent.EXTRA_TEXT, "${item.Name} ${item.Address} ${item.Mobile}")
                startActivity(Intent.createChooser(intent, "Choose one"))
            }

            // Load the doctor's picture using Glide
            Glide.with(this@DetailActivity)
                .load(item.Picture)
                .into(img)

            // Make Appointment button functionality
            makeBtn.setOnClickListener {
                val intent = Intent(this@DetailActivity, AppointmentActivity::class.java)
                intent.putExtra("doctor_id", item.Id)
                intent.putExtra("doctor_name", item.Name)
                intent.putExtra("doctor_image", item.Picture)
                intent.putExtra("Location", item.Location)
                intent.putExtra("fees", item.fees)
                intent.putExtra("patientId", patientId)
                startActivity(intent)
            }

            // Favorite button functionality
            binding.favBtn.setOnClickListener {
                toggleFavoriteStatus()
            }
        }
    }

    private fun loadFavoriteStatus() {
        val favoriteDoctors = favouriteViewModel.loadFavoriteDoctors(this, patientId)
        if (favoriteDoctors.contains(item)) {
            binding.favBtn.setImageResource(R.drawable.ic_fav)
        } else {
            binding.favBtn.setImageResource(R.drawable.ic_unfav)
        }
    }

    private fun toggleFavoriteStatus() {
        val favoriteDoctors = favouriteViewModel.loadFavoriteDoctors(this, patientId).toMutableList()
        if (favoriteDoctors.contains(item)) {
            favouriteViewModel.removeDoctorFromFavorites(this, patientId, item)
            binding.favBtn.setImageResource(R.drawable.ic_unfav)
        } else {
            favouriteViewModel.addDoctorToFavorites(this, patientId, item)
            binding.favBtn.setImageResource(R.drawable.ic_fav)
        }
    }

}
