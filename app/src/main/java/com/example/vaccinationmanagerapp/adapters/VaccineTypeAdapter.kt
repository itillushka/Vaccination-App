package com.example.vaccinationmanagerapp.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

class VaccineTypeAdapter(private val vaccineTypes: List<String>) :
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
        val vaccineType = vaccineTypes[position]
        holder.button.text = vaccineType
        holder.button.contentDescription = "Vaccine type: $vaccineType"
        if (position == selectedPosition) {
            holder.button.setBackgroundResource(R.drawable.gradient_rounded_button)
            holder.button.setTextColor(ContextCompat.getColor(holder.button.context, R.color.white))
        } else {
            holder.button.setBackgroundResource(R.drawable.button_with_blue_stroke)
            holder.button.setTextColor(ContextCompat.getColor(holder.button.context, R.color.blue_500))
        }
        holder.button.setOnClickListener {
            selectedPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = vaccineTypes.size

    // Method to get the selected vaccine type
    fun getSelectedVaccineType(): String? {
        return if (selectedPosition != -1) vaccineTypes[selectedPosition] else null
    }
}