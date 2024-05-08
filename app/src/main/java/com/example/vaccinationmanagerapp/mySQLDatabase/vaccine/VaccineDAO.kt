package com.example.vaccinationmanagerapp.mySQLDatabase.vaccine

/**
 * Interface for Vaccine Data Access Object.
 * This interface defines the methods for interacting with the Vaccine data in the database.
 */
interface VaccineDAO {

    /**
     * Inserts a new vaccine into the database.
     * @param vaccine The vaccine to be inserted.
     * @return Boolean indicating success or failure.
     */
    fun insertVaccine(vaccine: Vaccine): Boolean

    /**
     * Deletes a vaccine from the database.
     * @param vaccine The vaccine to be deleted.
     * @return Boolean indicating success or failure.
     */
    fun deleteVaccine(vaccine: Vaccine): Boolean

    /**
     * Retrieves all vaccines from the database.
     * @return List of all vaccine names.
     */
    fun getAllVaccines(): List<String>

    /**
     * Deletes a vaccine from the database by its name.
     * @param vaccine_name The name of the vaccine to be deleted.
     * @return Boolean indicating success or failure.
     */
    fun deleteVaccineByName(vaccine_name : String) : Boolean
}