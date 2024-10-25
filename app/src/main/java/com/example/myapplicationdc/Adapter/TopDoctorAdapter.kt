package com.example.myapplicationdc.Adapters

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
import com.example.myapplicationdc.databinding.ViewholderTopDoctorBinding

class TopDoctorAdapter(private val items: MutableList<DoctorModel>, private val patientId: Int?) :
    RecyclerView.Adapter<TopDoctorAdapter.Viewholder>() {

    private var context: Context? = null

    class Viewholder(val binding: ViewholderTopDoctorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        context = parent.context
        val binding = ViewholderTopDoctorBinding.inflate(LayoutInflater.from(context), parent, false)
        return Viewholder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val doctor = items[position]
        holder.binding.nameTxt.text = doctor.Name
        holder.binding.specialTxt.text = doctor.Special
        holder.binding.scoreTxt.text = doctor.Rating.toString()
        holder.binding.yearTxt.text = "${doctor.Experience} Years"

        // Load doctor image using Glide
        Glide.with(holder.itemView.context)
            .load(doctor.Picture)
            .apply(RequestOptions().transform(CenterCrop()))
            .into(holder.binding.img)

        // Navigate to DetailActivity with selected doctor and patientId
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("object", doctor)
                putExtra("patientId", patientId)
            }
            context?.startActivity(intent)
        }

    }
    fun updateDoctors(newDoctors: List<DoctorModel>) {
        items.clear() // Clear the current list
        items.addAll(newDoctors) // Add the new doctors
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }
}
