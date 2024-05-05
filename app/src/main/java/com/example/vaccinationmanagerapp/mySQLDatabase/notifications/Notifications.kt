package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

import java.sql.Timestamp


data class Notifications(
    val notification_id : Int? = null,
    val firebase_user_id : String? = null,
    val date_sent : Timestamp? = null,
    val title : String? = null,
    val description : String? = null
)
