package com.example.vaccinationmanagerapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.Notifications
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
import com.google.firebase.auth.FirebaseAuth
import java.sql.Timestamp
import java.util.concurrent.TimeUnit

/**
 * Worker class that runs periodically to remind the user of their upcoming appointments.
 *
 * This worker retrieves the appointment dates from the database and sends a notification
 * to the user if an appointment is less than 7 days away.
 */
class AppointmentReminderWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    /**
     * This method contains the work that needs to be performed.
     *
     * It retrieves the appointment dates from the database and sends a notification
     * to the user if an appointment is less than 7 days away.
     *
     * @return The result of the work, either [Result.success()] or [Result.failure()].
     */
    override fun doWork(): Result {
        // Retrieve the appointment dates from the database
        val connection = DBconnection.getConnection()
        val dbQueries = AppointmentDBQueries(connection)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""

        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }
        val appointmentDates = dbQueries.getAppointmentDate(firebaseUserId)

        // Get the current date
        val currentDate = System.currentTimeMillis()

        // Iterate over the appointment dates
        for (appointmentDate in appointmentDates) {
            // Calculate the difference in days between the appointment date and the current date
            val diff = appointmentDate.time - currentDate
            val diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

            // If the difference is less than 7 days, send a notification
            if (diffInDays < 7L) {
                val notificationService = MyFirebaseMessagingService()
                notificationService.generateNotification(applicationContext,"Appointment Reminder", "Your appointment is in $diffInDays days.")

                val firebaseAuth = FirebaseAuth.getInstance()
                val firebaseUser = firebaseAuth.currentUser
                var firebaseUserId : String = ""
                if (firebaseUser != null) {
                    firebaseUserId = firebaseUser.uid
                }
                val notificationDBQueries = NotificationsDBQueries(connection)
                val notification = Notifications(
                    firebase_user_id = firebaseUserId,
                    date_sent = Timestamp(System.currentTimeMillis()),
                    title = "Appointment Reminder",
                    description = "Your appointment is in $diffInDays days."
                )
                notificationDBQueries.insertNotifications(notification)
            }
        }

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}