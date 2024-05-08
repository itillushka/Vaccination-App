package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import java.sql.Date

/**
 * Interface for Appointment Data Access Object.
 * This interface defines the methods for interacting with the Appointment data in the database.
 */
interface AppointmentDAO {

    /**
     * Inserts a new appointment into the database.
     *
     * @param appointment The appointment to be inserted.
     * @return True if the operation was successful, false otherwise.
     */
    fun insertAppointment(appointment: Appointment): Boolean

    /**
     * Deletes an appointment from the database.
     *
     * @param appointment_id The ID of the appointment to be deleted.
     * @return True if the operation was successful, false otherwise.
     */
    fun deleteAppointment(appointment_id: Int): Boolean

    /**
     * Creates a new appointment in the database.
     *
     * @param vaccine_name The name of the vaccine for the appointment.
     * @param date The date of the appointment.
     * @param firebase_user_id The Firebase user ID of the user who has the appointment.
     * @return True if the operation was successful, false otherwise.
     */
    fun createAppointment(vaccine_name: String, date: String, firebase_user_id: String): Boolean

    /**
     * Cancels an appointment in the database.
     *
     * @param appointment_id The ID of the appointment to be cancelled.
     * @return True if the operation was successful, false otherwise.
     */
    fun cancelAppointment(appointment_id: Int): Boolean

    /**
     * Fetches the details of an appointment from the database.
     *
     * @param appointment_id The ID of the appointment.
     * @return A list of strings representing the details of the appointment.
     */
    fun fetchAppointmentDetails(appointment_id: Int) : List<String>

    /**
     * Fetches an appointment by its ID from the database for admin use.
     *
     * @param appointment_id The ID of the appointment.
     * @return A list of strings representing the details of the appointment, or null if the appointment does not exist.
     */
    fun fetchAppointmentByIdAdmin(appointment_id: Int): List<String>?

    /**
     * Fetches all appointments for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of appointments for the user.
     */
    fun getAppointmentsByUser(firebase_user_id: String): List<Appointment>

    /**
     * Fetches all records for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of appointments for the user.
     */
    fun getRecordsByUser(firebase_user_id: String): List<Appointment>

    /**
     * Checks if a user has an active appointment for a specific vaccine in the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @param vaccine_name The name of the vaccine.
     * @return True if the user has an active appointment for the vaccine, false otherwise.
     */
    fun hasActiveAppointment(firebase_user_id: String, vaccine_name: String): Boolean

    /**
     * Fetches the date of an appointment for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of dates for the user's appointments.
     */
    fun getAppointmentDate(firebase_user_id: String): List<Date>

    /**
     * Adds a vaccination record for a user in the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @param vaccine_name The name of the vaccine.
     * @param dose The dose number of the vaccine.
     * @param date The date of the vaccination.
     * @return True if the operation was successful, false otherwise.
     */
    fun addVaccinationRecord(firebase_user_id: String, vaccine_name: String, dose: Int, date: String): Boolean

}