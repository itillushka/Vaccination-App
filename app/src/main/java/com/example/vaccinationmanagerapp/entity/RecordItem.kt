package com.example.vaccinationmanagerapp.entity

import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status

/**
 * A data class that represents a record item in the list.
 *
 * @property appointment_id The unique identifier of the record.
 * @property vaccine_name The name of the vaccine for the record.
 * @property date The date of the record.
 * @property dose The dose number of the record.
 * @property status The status of the record.
 * @property number_of_doses The total number of doses for the vaccine.
 * @property time_between_doses The time between doses for the vaccine.
 *
 * @constructor Creates an instance of RecordItem with the provided properties.
 */
data class RecordItem(
    val appointment_id: Int,
    val vaccine_name: String,
    val date: String,
    val dose: Int,
    var status: status,
    val number_of_doses: Int,
    var time_between_doses: Int
)