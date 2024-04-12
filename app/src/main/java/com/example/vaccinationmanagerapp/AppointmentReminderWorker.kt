package com.example.vaccinationmanagerapp

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.TimeUnit

class AppointmentReminderWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        // Retrieve the appointment date from the database
        val connection = DBconnection.getConnection()
        val dbQueries = AppointmentDBQueries(connection)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""

        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }
        val appointmentDate = dbQueries.getAppointmentDate(firebaseUserId)

        // Check if the appointment date is 7 days from today
        val currentDate = System.currentTimeMillis()
        val diff = appointmentDate!!.time - currentDate
        val diffInDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)

        if (diffInDays == 7L) {
            // If it's 7 days from today, send a notification
            val notificationService = MyFirebaseMessagingService()
            notificationService.generateNotification(applicationContext,"Appointment Reminder", "Your appointment is in 7 days.")
        }

        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }
}