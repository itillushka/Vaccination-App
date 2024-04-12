package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import java.sql.Date
import java.sql.Timestamp

data class Appointment(
    val appointment_id: Int? = null,
    val user_id: Int? = null,
    val vaccine_id: Int? = null,
    val status: status? = null,
    val dose : Int? = null,
    val date : Timestamp? = null,
    val vaccine_name: String? = null,
)
