package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.Appointment

interface NotificationsDAO {

    fun insertNotifications(notification: Notifications): Boolean
    fun getNotifications(firebase_user_id: String): List<Notifications>
}