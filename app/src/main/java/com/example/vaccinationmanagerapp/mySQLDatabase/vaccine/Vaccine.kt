package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine

/**
 * Data class representing a Vaccine.
 * @property vaccine_id The ID of the vaccine.
 * @property vaccine_name The name of the vaccine.
 * @property number_of_doses The number of doses required for the vaccine.
 * @property time_between_doses The time between doses for the vaccine.
 */
data class Vaccine(
    val vaccine_id : Int? = null,
    val vaccine_name: String? = null,
    val number_of_doses: Int? = null,
    val time_between_doses: Int? = null
)