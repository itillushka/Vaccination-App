package com.example.vaccinationmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

data class AppointmentItem(val appointmentText: String)

class AppointmentsListAdapter(private val appointmentList: List<AppointmentItem>) :
    RecyclerView.Adapter<AppointmentsListAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appmName: TextView = itemView.findViewById(R.id.appointmentName)
        val appointmentStatus: ImageView = itemView.findViewById(R.id.appointmentStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.appointment_list_item,
            parent, false
        )
        return AppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointmentList[position]
        holder.appmName.text = currentItem.appointmentText

        // Change the appointmentStatus ImageView for the second item in the list
        if (position == 1) {
            holder.appointmentStatus.setImageResource(R.drawable.completed_appm_icon)
        } else {
            holder.appointmentStatus.setImageResource(R.drawable.upcoming_appm_icon)
        }
    }

    override fun getItemCount() = appointmentList.size
}