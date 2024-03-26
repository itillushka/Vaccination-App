package com.example.vaccinationmanagerapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

class TimeSlotAdapter(private val timeSlots: List<Pair<String, Boolean>>) :
    RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    class TimeSlotViewHolder(val button: Button) : RecyclerView.ViewHolder(button)
    // Store the selected position
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false) as Button
        return TimeSlotViewHolder(button)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val (time, isAvailable) = timeSlots[position]
        holder.button.text = time
        holder.button.contentDescription = "Appointment time slot for $time"
        if (isAvailable) {
            if (position == selectedPosition) {
                // If this position is the selected position, change the button's design to button_with_blue_stroke.xml
                holder.button.setBackgroundResource(R.drawable.gradient_rounded_button)
                holder.button.setTextColor(ContextCompat.getColor(holder.button.context, R.color.white))
            } else {
                holder.button.setBackgroundResource(R.drawable.button_with_blue_stroke)
                holder.button.setTextColor(ContextCompat.getColor(holder.button.context, R.color.blue_500))
            }
        } else {
            holder.button.setBackgroundResource(R.drawable.unavailable_button)
            holder.button.setTextColor(ContextCompat.getColor(holder.button.context, R.color.unavailable))
        }
        holder.button.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged() // Notify the adapter to update the button designs
        }
    }

    override fun getItemCount() = timeSlots.size

    // Method to get the selected time slot
    fun getSelectedTimeSlot(): Pair<String, Boolean>? {
        return if (selectedPosition != -1) timeSlots[selectedPosition] else null
    }
}