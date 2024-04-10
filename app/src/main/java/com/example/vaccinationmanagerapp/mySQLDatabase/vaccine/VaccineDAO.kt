package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine


interface VaccineDAO {

    fun insertVaccine(vaccine: Vaccine): Boolean

    fun deleteVaccine(vaccine: Vaccine): Boolean

    fun deleteVaccineByName(vaccine_name : String) : Boolean
}