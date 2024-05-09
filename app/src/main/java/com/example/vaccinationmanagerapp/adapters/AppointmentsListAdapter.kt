package com.example.vaccinationmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.MyFirebaseMessagingService
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.entity.AppointmentItem
import com.example.vaccinationmanagerapp.fragments.AppointmentDetailsDialogFragment
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.Notifications
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Timestamp

/**
 * A RecyclerView.Adapter that is used to display a list of appointments in a RecyclerView.
 *
 * @property appointmentList The list of appointments to display in the RecyclerView. Each appointment is represented as an instance of [AppointmentItem].
 *
 * @constructor Creates an instance of AppointmentsListAdapter with the provided list of appointments.
 */

class AppointmentsListAdapter(private val appointmentList: List<AppointmentItem>) :
    RecyclerView.Adapter<AppointmentsListAdapter.AppointmentViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @property appmName The TextView that displays the name of the vaccine for the appointment.
     * @property appointmentStatus The ImageView that displays the status of the appointment.
     * @property dateAppText The TextView that displays the date of the appointment.
     * @property doseAppText The TextView that displays the dose number of the appointment.
     * @property cancelBookingButton The Button that allows the user to cancel the appointment.
     */
    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val appmName: TextView = itemView.findViewById(R.id.appointmentName)
        val appointmentStatus: ImageView = itemView.findViewById(R.id.appointmentStatus)
        val dateAppText: TextView = itemView.findViewById(R.id.dateAppText)
        val doseAppText: TextView = itemView.findViewById(R.id.doseAppText)
        val cancelBookingButton: Button = itemView.findViewById(R.id.cancelBookingButton)
        init {
            val seeDetailsButton: Button = itemView.findViewById(R.id.seeDetailsButton)
            seeDetailsButton.setOnClickListener {
                val appointment_id = appointmentList[adapterPosition].appointment_id
                val dialogFragment = AppointmentDetailsDialogFragment(appointment_id)
                val fragmentManager = (itemView.context as AppCompatActivity).supportFragmentManager
                dialogFragment.show(fragmentManager, "AppointmentDetailsDialogFragment")
            }
        }
    }
    /**
     * Called when RecyclerView needs a new [AppointmentViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [AppointmentViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.appointment_list_item,
            parent, false
        )
        return AppointmentViewHolder(itemView)
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [AppointmentViewHolder] that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val currentItem = appointmentList[position]
        holder.appmName.text = currentItem.vaccine_name
        holder.dateAppText.text = currentItem.date
        holder.doseAppText.text = "Dose: ${currentItem.dose}"
        if (currentItem.status == status.Completed) {
            holder.appointmentStatus.setImageResource(R.drawable.completed_appm_icon)
        } else if (currentItem.status == status.Canceled) {
            holder.appointmentStatus.setImageResource(R.drawable.canceled_appm_icon)
            holder.cancelBookingButton.setTextColor(R.color.gray)
            holder.cancelBookingButton.setBackgroundResource(R.drawable.unavailable_button)
            holder.cancelBookingButton.text = "Canceled"
        } else {
            holder.appointmentStatus.setImageResource(R.drawable.upcoming_appm_icon)
        }
        holder.cancelBookingButton.setOnClickListener {
            val appointmentId = currentItem.appointment_id

            CoroutineScope(Dispatchers.IO).launch {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)

                val result = dbQueries.cancelAppointment(appointmentId)

                connection.close()

                if (result) {
                    val firebaseAuth = FirebaseAuth.getInstance()
                    val firebaseUser = firebaseAuth.currentUser
                    var firebaseUserId : String = ""
                    if (firebaseUser != null) {
                        firebaseUserId = firebaseUser.uid
                    }
                    val connection = DBconnection.getConnection()
                    // Insert the notification into the database
                    val notificationDBQueries = NotificationsDBQueries(connection)
                    val notification = Notifications(
                        firebase_user_id = firebaseUserId,
                        date_sent = Timestamp(System.currentTimeMillis()),
                        title = "Appointment Reminder",
                        description = "Your appointment is cancelled!"
                    )
                    notificationDBQueries.insertNotifications(notification)

                    withContext(Dispatchers.Main) {
                        holder.appointmentStatus.setImageResource(R.drawable.canceled_appm_icon)
                        currentItem.status = status.Canceled
                        holder.cancelBookingButton.setTextColor(R.color.gray)
                        holder.cancelBookingButton.setBackgroundResource(R.drawable.unavailable_button)
                        holder.cancelBookingButton.text = "Canceled"
                        val notificationService = MyFirebaseMessagingService()
                        notificationService.generateNotification(holder.itemView.context,"Appointment Reminder", "Your appointment is cancelled!")
                    }
                } else {
                    // Show an error message
                }
            }


        }
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = appointmentList.size
}