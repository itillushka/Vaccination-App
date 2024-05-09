package com.example.vaccinationmanagerapp.models

/**
 * Data class representing a Vaccination Record.
 *
 * @property vaccineName The name of the vaccine administered.
 * @property dose The dose number of the vaccine administered.
 * @property date The date when the vaccine was administered.
 * @property nextDose The date when the next dose of the vaccine is due.
 */
data class VaccinationRecord(
    val vaccineName: String,
    val dose: Int,
    val date: String,
    val nextDose: String
)
