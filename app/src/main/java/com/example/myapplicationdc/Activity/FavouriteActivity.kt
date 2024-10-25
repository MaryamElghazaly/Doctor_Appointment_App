package com.example.myapplicationdc.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationdc.Adapter.FavoriteDoctorAdapter
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.favouriteViewModel
import com.example.myapplicationdc.databinding.ActivityFavouriteBinding
import com.google.android.material.textview.MaterialTextView

class FavouriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: MaterialTextView
    private lateinit var adapter: FavoriteDoctorAdapter
    private lateinit var binding: ActivityFavouriteBinding
    private var favoriteDoctors: MutableList<DoctorModel> = mutableListOf()
    private val favouriteViewModel: favouriteViewModel by viewModels()
    var patientId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = findViewById(R.id.recyclerView)
        emptyView = findViewById(R.id.emptyFavoriteView)

        adapter = FavoriteDoctorAdapter(this, favoriteDoctors) { doctor ->
            showConfirmationDialog(doctor)
        }
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        patientId = intent.getIntExtra("patientId", -1)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadFavorites()
    }

    private fun loadFavorites() {
        favoriteDoctors = favouriteViewModel.loadFavoriteDoctors(this, patientId).toMutableList()
        adapter.updateList(favoriteDoctors)
        updateEmptyView()
    }

    private fun updateEmptyView() {
        if (favoriteDoctors.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun showConfirmationDialog(doctor: DoctorModel) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Remove Favorite")
            .setMessage("Are you sure you want to remove ${doctor.Name} from favorites?")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                removeDoctorFromFavorites(doctor)
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun removeDoctorFromFavorites(doctor: DoctorModel) {
        favoriteDoctors = favouriteViewModel.removeDoctorFromFavorites(this, patientId, doctor).toMutableList()
        adapter.updateList(favoriteDoctors)
        updateEmptyView()
    }
}
