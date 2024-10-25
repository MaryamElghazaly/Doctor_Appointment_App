package com.example.myapplicationdc.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationdc.Domain.DoctorModel
import com.example.myapplicationdc.R

class DoctorAppointmentAdapter(
    private val days: List<String>,
    private val onDayClick: (String) -> Unit // Add this parameter for the click listener
) : RecyclerView.Adapter<DoctorAppointmentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dayTextView: TextView = view.findViewById(R.id.day_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.availabledays_viewholder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dayName = days[position]
        holder.dayTextView.text = dayName // Set the day text

        // Set a click listener to handle clicks on each day item
        holder.itemView.setOnClickListener {
            onDayClick(dayName) // Pass the clicked day name to the listener
        }
    }

    override fun getItemCount(): Int = days.size
}
