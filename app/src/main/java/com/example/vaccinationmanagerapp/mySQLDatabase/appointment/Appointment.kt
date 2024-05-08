package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import java.sql.Timestamp

/**
 * This data class represents an Appointment in the system.
 *
 * @property appointment_id The unique identifier of the appointment.
 * @property user_id The unique identifier of the user who has the appointment.
 * @property vaccine_id The unique identifier of the vaccine to be administered in the appointment.
 * @property status The status of the appointment (Scheduled, Completed, Canceled).
 * @property dose The dose number of the vaccine to be administered in the appointment.
 * @property date The timestamp when the appointment is scheduled.
 * @property vaccine_name The name of the vaccine to be administered in the appointment.
 */
data class Appointment(
    val appointment_id: Int? = null,
    val user_id: Int? = null,
    val vaccine_id: Int? = null,
    val status: status? = null,
    val dose : Int? = null,
    val date : Timestamp? = null,
    val vaccine_name: String? = null,
    val number_of_doses: Int? = null,
    val time_between_doses: Int? = null
)
