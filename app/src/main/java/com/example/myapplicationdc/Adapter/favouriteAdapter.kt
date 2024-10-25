package com.example.myapplicationdc.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R

class FavoriteDoctorAdapter(
    private val context: Context,
    private var doctorList: List<DoctorModel>,
    private val onUnfavoriteClick: (DoctorModel) -> Unit
) : RecyclerView.Adapter<FavoriteDoctorAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorNameTextView: TextView = view.findViewById(R.id.doctorNameTextView)
        val specialTextView: TextView = view.findViewById(R.id.special)
        val doctorImageView: ImageView = view.findViewById(R.id.doctorImageView)
        val unfavIcon: ImageButton = view.findViewById(R.id.unfavIcon)

        fun bind(doctor: DoctorModel) {
            doctorNameTextView.text = doctor.Name
            specialTextView.text = doctor.Special
            // Load image using Glide or similar library
            Glide.with(context)
                .load(doctor.Picture)
                .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image
                .into(doctorImageView)

            unfavIcon.setOnClickListener {
                onUnfavoriteClick(doctor)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_viewholder, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.bind(doctor)

    }

    override fun getItemCount(): Int = doctorList.size

    fun updateList(newList: List<DoctorModel>) {
        doctorList = newList
        notifyDataSetChanged()
    }
}
