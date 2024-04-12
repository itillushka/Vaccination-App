package com.example.vaccinationmanagerapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

class VaccineTypeAdapter(private val vaccineAvailability: List<Pair<String, Boolean>>) :
    RecyclerView.Adapter<VaccineTypeAdapter.VaccineTypeViewHolder>() {

    class VaccineTypeViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    // Variable to keep track of the selected position
    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineTypeViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccine_type, parent, false) as Button
        return VaccineTypeViewHolder(button)
    }

    override fun onBindViewHolder(holder: VaccineTypeViewHolder, position: Int) {
        val (vaccine, isAvailable) = vaccineAvailability[position]
        holder.button.text = vaccine
        holder.button.contentDescription = "Vaccine type: $vaccine"
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

    override fun getItemCount() = vaccineAvailability.size

    // Method to get the selected vaccine type
    fun getSelectedVaccineType(): String? {
        return if (selectedPosition != -1) vaccineAvailability[selectedPosition].first else null
    }
}