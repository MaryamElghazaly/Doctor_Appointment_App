package com.example.myapplicationdc.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.example.myapplicationdc.Activity.DetailActivity
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R
import com.example.myapplicationdc.ViewModel.favouriteViewModel
import com.example.myapplicationdc.databinding.ViewholderTopDoctor2Binding

class TopDoctorAdapter2(
    private val items: MutableList<DoctorModel>,
    private val patientId: Int?
) : RecyclerView.Adapter<TopDoctorAdapter2.Viewholder>() {

    private var context: Context? = null
    private val favouriteViewModel = favouriteViewModel()

    class Viewholder(val binding: ViewholderTopDoctor2Binding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderTopDoctor2Binding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val doctor = items[position]
        holder.binding.nameTxt.text = doctor.Name
        holder.binding.special.text = doctor.Special
        holder.binding.ScoreTxt.text = doctor.Rating.toString()
        holder.binding.ratingBar.rating = doctor.Rating.toFloat()
        holder.binding.degreeTxt.text = "Professional Doctor"

        // Load doctor image using Glide
        Glide.with(holder.itemView.context)
            .load(doctor.Picture)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.img)

        // Check if the doctor is already in favorites and update the icon
        updateFavoriteIcon(holder, doctor)

        // Navigate to DetailActivity with selected doctor and patientId
        holder.binding.makeBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", doctor)
                putExtra("patientId", patientId)
            }
            context?.startActivity(intent)
        }

        // Favorite button functionality
        holder.binding.favBtn.setOnClickListener {
            toggleFavoriteStatus(holder, doctor)
        }
    }

    private fun toggleFavoriteStatus(holder: Viewholder, doctor: DoctorModel) {
        val favoriteDoctors = favouriteViewModel.loadFavoriteDoctors(context!!, patientId!!).toMutableList()
        if (favoriteDoctors.contains(doctor)) {
            favouriteViewModel.removeDoctorFromFavorites(context!!, patientId!!, doctor)
            holder.binding.favBtn.setImageResource(R.drawable.ic_unfav) // Set to unfavored icon
        } else {
            favouriteViewModel.addDoctorToFavorites(context!!, patientId!!, doctor)
            holder.binding.favBtn.setImageResource(R.drawable.ic_fav) // Set to favored icon
        }
    }

    private fun updateFavoriteIcon(holder: Viewholder, doctor: DoctorModel) {
        val favoriteDoctors = favouriteViewModel.loadFavoriteDoctors(context!!, patientId!!)
        if (favoriteDoctors.contains(doctor)) {
            holder.binding.favBtn.setImageResource(R.drawable.ic_fav) // Favorite icon
        } else {
            holder.binding.favBtn.setImageResource(R.drawable.ic_unfav) // Unfavorite icon
        }
    }
}
