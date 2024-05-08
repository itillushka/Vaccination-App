package com.example.vaccinationmanagerapp.adapters


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
/**
 * A RecyclerView.Adapter that is used to display a list of vaccine types in a RecyclerView.
 *
 * @property vaccineAvailability The list of vaccine types to display in the RecyclerView. Each vaccine type is represented as a Pair of a String and a Boolean.
 *
 * @constructor Creates an instance of VaccineTypeAdapter with the provided list of vaccine types.
 */
class VaccineTypeAdapter(private val vaccineAvailability: List<Pair<String, Boolean>>) :
    RecyclerView.Adapter<VaccineTypeAdapter.VaccineTypeViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @property button The Button that displays the vaccine type.
     */
    class VaccineTypeViewHolder(val button: Button) : RecyclerView.ViewHolder(button)

    // Variable to keep track of the selected position
    private var selectedPosition = -1
    /**
     * Called when RecyclerView needs a new [VaccineTypeViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [VaccineTypeViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VaccineTypeViewHolder {
        val button = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vaccine_type, parent, false) as Button
        return VaccineTypeViewHolder(button)
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [VaccineTypeViewHolder] that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("NotifyDataSetChanged")
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
    /**
     * Returns the number of items in the list.
     *
     * @return The number of items in the list.
     */
    override fun getItemCount() = vaccineAvailability.size

    /**
     * Returns the selected vaccine type.
     *
     * @return The selected vaccine type, or null if no vaccine type is selected.
     */
    fun getSelectedVaccineType(): String? {
        return if (selectedPosition != -1) vaccineAvailability[selectedPosition].first else null
    }
}