package com.example.myapplicationdc.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplicationdc.Domain.Appointment
import com.example.myapplicationdc.databinding.ViewholderHistoryBinding

class AppointmentAdapter(
    var items: List<Appointment>,
    private val onDelete: (Appointment, Int) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.ViewHolder>() {
    private var context: Context? = null

    class ViewHolder(val binding: ViewholderHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = ViewholderHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appointment = items[position]
        holder.binding.doctorNameTextView.text = appointment.doctorName
        holder.binding.appointmentDayTextView.text = appointment.appointmentDate

        // Load the doctor's image
        Glide.with(holder.itemView.context)
            .load(appointment.doctorImage)
            .into(holder.binding.doctorImageView)

        // Set delete button click listener
        holder.binding.deleteIcon.setOnClickListener {
            onDelete(appointment, position) // Trigger the delete callback with appointment and position
        }
    }

    override fun getItemCount(): Int = items.size

    // Method to update data in the adapter
    fun updateData(newItems: List<Appointment>) {
        this.items = newItems
        notifyDataSetChanged() // Notify the adapter to refresh the list
    }
}
