package com.example.vaccinationmanagerapp.entity

import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status

/**
 * A data class that represents an appointment item in the list.
 *
 * @property appointment_id The unique identifier of the appointment.
 * @property vaccine_name The name of the vaccine for the appointment.
 * @property date The date of the appointment.
 * @property dose The dose number of the appointment.
 * @property status The status of the appointment.
 *
 * @constructor Creates an instance of AppointmentItem with the provided properties.
 */
data class AppointmentItem(
    val appointment_id: Int,
    val vaccine_name: String,
    val date: String,
    val dose: Int,
    var status: status
)
