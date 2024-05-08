package com.example.vaccinationmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

/**
 * A RecyclerView.Adapter that is used to display a list of time slots in a RecyclerView.
 *
 * @property timeSlots The list of time slots to display in the RecyclerView. Each time slot is represented as a Pair of a String and a Boolean.
 *
 * @constructor Creates an instance of TimeSlotAdapter with the provided list of time slots.
 */
class TimeSlotAdapter(private val timeSlots: List<Pair<String, Boolean>>) :
    RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @property button The Button that displays the time slot.
     */
    class TimeSlotViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    private var selectedPosition = -1
    /**
     * Called when RecyclerView needs a new [TimeSlotViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [TimeSlotViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_time_slot, parent, false) as Button
        return TimeSlotViewHolder(button)
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [TimeSlotViewHolder] that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("NotifyDataSetChanged")
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
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = timeSlots.size

    /**
     * Returns the selected time slot and whether it is available.
     *
     * @return A Pair of a String and a Boolean representing the selected time slot and whether it is available.
     */
    fun getSelectedTimeSlot(): Pair<String, Boolean>? {
        return if (selectedPosition != -1) timeSlots[selectedPosition] else null
    }
}