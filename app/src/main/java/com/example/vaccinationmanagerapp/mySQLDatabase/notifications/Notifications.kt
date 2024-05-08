package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

import java.sql.Timestamp

/**
 * Data class representing a Notification.
 * @property notification_id The ID of the notification.
 * @property firebase_user_id The Firebase user ID of the user who will receive the notification.
 * @property date_sent The date when the notification was sent.
 * @property title The title of the notification.
 * @property description The description of the notification.
 */
data class Notifications(
    val notification_id : Int? = null,
    val firebase_user_id : String? = null,
    val date_sent : Timestamp? = null,
    val title : String? = null,
    val description : String? = null
)
