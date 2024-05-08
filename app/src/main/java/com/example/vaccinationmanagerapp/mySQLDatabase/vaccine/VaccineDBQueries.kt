package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine

import java.sql.Connection

/**
 * Class for Vaccine Database Queries.
 * This class implements the VaccineDAO interface and defines the methods for interacting with the Vaccine data in the database.
 * @property connection The connection to the database.
 */
class VaccineDBQueries(private val connection: Connection) : VaccineDAO {

    /**
     * Inserts a new vaccine into the database.
     * @param vaccine The vaccine to be inserted.
     * @return Boolean indicating success or failure.
     */
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

    /**
     * Deletes a vaccine from the database.
     * @param vaccine The vaccine to be deleted.
     * @return Boolean indicating success or failure.
     */
    override fun deleteVaccine(vaccine: Vaccine): Boolean {
        val call = "{CALL deleteVaccine(?)}"
        val statement = connection.prepareCall(call)
        vaccine.vaccine_id?.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Retrieves all vaccines from the database.
     * @return List of all vaccine names.
     */
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

    /**
     * Deletes a vaccine from the database by its name.
     * @param vaccine_name The name of the vaccine to be deleted.
     * @return Boolean indicating success or failure.
     */
    override fun deleteVaccineByName(vaccine_name: String): Boolean {
        val call = "{CALL deleteVaccineByName(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, vaccine_name)
        val result = !statement.execute()
        statement.close()
        return result
    }
}