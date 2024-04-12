package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine

import java.sql.Connection

class VaccineDBQueries(private val connection: Connection) : VaccineDAO {

    override fun insertVaccine(vaccine: Vaccine): Boolean {
        val call = "{CALL insertVaccine(?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, vaccine.vaccine_name)
        vaccine.number_of_doses?.let { statement.setInt(2, it) }
        vaccine.time_between_doses?.let { statement.setInt(3, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun deleteVaccine(vaccine: Vaccine): Boolean {
        val call = "{CALL deleteVaccine(?)}"
        val statement = connection.prepareCall(call)
        vaccine.vaccine_id?.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun deleteVaccineByName(vaccine_name: String): Boolean {
        val call = "{CALL deleteVaccineByName(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, vaccine_name)
        val result = !statement.execute()
        statement.close()
        return result
    }
}