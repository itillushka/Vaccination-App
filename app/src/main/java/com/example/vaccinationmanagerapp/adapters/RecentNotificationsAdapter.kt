package com.example.vaccinationmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
/**
 * A data class that represents a notification item in the list.
 *
 * @property notificationTitle The title of the notification.
 * @property notificationBody The body of the notification.
 * @property notificationDate The date of the notification.
 *
 * @constructor Creates an instance of NotificationItem with the provided properties.
 */
data class NotificationItem(val notificationTitle: String, val notificationBody: String, val notificationDate: String)
/**
 * A RecyclerView.Adapter that is used to display a list of notifications in a RecyclerView.
 *
 * @property notifications The list of notifications to display in the RecyclerView. Each notification is represented as an instance of [NotificationItem].
 *
 * @constructor Creates an instance of RecentNotificationsAdapter with the provided list of notifications.
 */
class RecentNotificationsAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<RecentNotificationsAdapter.NotificationViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @property notificationTitle The TextView that displays the title of the notification.
     * @property notificationBody The TextView that displays the body of the notification.
     * @property notificationDate The TextView that displays the date of the notification.
     */
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val notificationTitle: TextView = itemView.findViewById(R.id.textViewNotificationTitle)
        val notificationBody: TextView = itemView.findViewById(R.id.textViewNotificationBody)
        val notificationDate: TextView = itemView.findViewById(R.id.textViewNotificationsDate)
    }
    /**
     * Called when RecyclerView needs a new [NotificationViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [NotificationViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [NotificationViewHolder] that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.notificationTitle.text = notification.notificationTitle
        holder.notificationBody.text = notification.notificationBody
        holder.notificationDate.text = notification.notificationDate
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = notifications.size
}