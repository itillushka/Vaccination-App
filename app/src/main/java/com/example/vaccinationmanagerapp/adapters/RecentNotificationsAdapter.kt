package com.example.vaccinationmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R

data class NotificationItem(val notificationTitle: String, val notificationBody: String, val notificationDate: String)

class RecentNotificationsAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<RecentNotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationTitle: TextView = itemView.findViewById(R.id.textViewNotificationTitle)
        val notificationBody: TextView = itemView.findViewById(R.id.textViewNotificationBody)
        val notificationDate: TextView = itemView.findViewById(R.id.textViewNotificationsDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.notificationTitle.text = notification.notificationTitle
        holder.notificationBody.text = notification.notificationBody
        holder.notificationDate.text = notification.notificationDate
    }

    override fun getItemCount() = notifications.size
}