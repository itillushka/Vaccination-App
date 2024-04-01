package com.example.vaccinationmanagerapp.models

data class VaccinationRecord(
    val vaccineName: String,
    val dose: Int,
    val date: String,
    val nextDose: String
)
