package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine

data class Vaccine(
    val vaccine_id : Int? = null,
    val vaccine_name: String? = null,
    val number_of_doses: Int? = null,
    val time_between_doses: Int? = null
)
