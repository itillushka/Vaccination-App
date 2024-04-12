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

    override fun getAllVaccines(): List<String> {
        val call = "SELECT vaccine_name FROM Vaccine"
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(call)

        val vaccineNames = mutableListOf<String>()
        while (resultSet.next()) {
            vaccineNames.add(resultSet.getString("vaccine_name"))
        }

        resultSet.close()
        statement.close()

        return vaccineNames
}
}