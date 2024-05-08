package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import android.annotation.SuppressLint
import java.sql.Connection
import java.sql.Date
import java.sql.SQLException
import java.sql.Timestamp
import java.sql.Types
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Class for Appointment Database Queries.
 * This class implements the AppointmentDAO interface and defines the methods for interacting with the Appointment data in the database.
 * @property connection The connection to the database.
 */
class AppointmentDBQueries(private val connection: Connection) : AppointmentDAO {

    /**
     * Inserts a new appointment into the database.
     *
     * @param appointment The appointment to be inserted.
     * @return True if the operation was successful, false otherwise.
     */
    override fun insertAppointment(appointment: Appointment): Boolean {
        return try {
            val call = "{CALL insertAppointment(?, ?, ?, ?, ?)}"
            val statement = connection.prepareCall(call)
            appointment.user_id?.let { statement.setInt(1, it) }
            appointment.vaccine_id?.let { statement.setInt(2, it) }
            statement.setString(3, appointment.status?.name ?: "Scheduled")
            appointment.dose?.let { statement.setInt(4, it) }
            statement.setTimestamp(5, appointment.date)
            val result = !statement.execute()
            statement.close()
            return result
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }

    }

    /**
     * Deletes an appointment from the database.
     *
     * @param appointment_id The ID of the appointment to be deleted.
     * @return True if the operation was successful, false otherwise.
     */
    override fun deleteAppointment(appointment_id: Int): Boolean {
        val call = "{CALL deleteAppointment(?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, appointment_id)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Creates a new appointment in the database.
     *
     * @param vaccine_name The name of the vaccine for the appointment.
     * @param date The date of the appointment.
     * @param firebase_user_id The Firebase user ID of the user who has the appointment.
     * @return True if the operation was successful, false otherwise.
     */
    @SuppressLint("SimpleDateFormat")
    override fun createAppointment(vaccine_name: String, date: String, firebase_user_id: String): Boolean {
        val getUserVaccineDose = "{CALL getUserVaccineDose(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(getUserVaccineDose)

        // Set parameters
        statement.setString(1, firebase_user_id)
        statement.setString(2, vaccine_name)
        statement.registerOutParameter(3, Types.INTEGER)
        statement.registerOutParameter(4, Types.INTEGER)
        statement.registerOutParameter(5, Types.INTEGER)

        statement.execute()
        val user_id = statement.getInt(3)
        val vaccine_id = statement.getInt(4)
        val dose = statement.getInt(5) + 1


        statement.close()

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val utilDate = format.parse(date)
        val sqlTimestamp = Timestamp(utilDate!!.time)

        // Create new Appointment object
        val appointment = Appointment(
            user_id = user_id,
            vaccine_id = vaccine_id,
            status = status.Scheduled,
            dose = dose,
            date = sqlTimestamp // Convert string to Date
        )

        // Insert the new Appointment into the database
        return insertAppointment(appointment)
    }

    /**
     * Fetches all appointments for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of appointments for the user.
     */
    override fun getAppointmentsByUser(firebase_user_id: String): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val call = "{CALL getAppointmentsByUser(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val hasResultSet = statement.execute()
        if (hasResultSet) {
            val result = statement.resultSet
            while (result.next()) {
                val appointment = Appointment(
                    appointment_id = result.getInt("appointment_id"),
                    user_id = result.getInt("user_id"),
                    vaccine_id = result.getInt("vaccine_id"),
                    status = status.valueOf(result.getString("status")),
                    dose = result.getInt("dose"),
                    date = result.getTimestamp("date"),
                    vaccine_name = result.getString("vaccine_name")
                )
                appointments.add(appointment)
            }
        }
        statement.close()
        val callUpdate = "{CALL updateStatusToCompleted()}"
        val statementUpdate = connection.prepareCall(callUpdate)
        statementUpdate.executeQuery()
        statementUpdate.close()
        return appointments
    }

    /**
     * Fetches all records for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of appointments for the user.
     */
    override fun getRecordsByUser(firebase_user_id: String): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val call = "{CALL fetchUserRecords(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val result = statement.executeQuery()
        while (result.next()) {
            val appointment = Appointment(
                appointment_id = result.getInt("appointment_id"),
                user_id = result.getInt("user_id"),
                vaccine_id = result.getInt("vaccine_id"),
                status = status.valueOf(result.getString("status")),
                dose = result.getInt("dose"),
                date = result.getTimestamp("date"),
                vaccine_name = result.getString("vaccine_name")
            )
            appointments.add(appointment)
        }
        statement.close()
        return appointments
    }

    /**
     * Checks if a user has an active appointment for a specific vaccine in the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @param vaccine_name The name of the vaccine.
     * @return True if the user has an active appointment for the vaccine, false otherwise.
     */
    override fun hasActiveAppointment(firebase_user_id: String, vaccine_name: String): Boolean {
        val call = "{CALL hasActiveAppointment(?,?,?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        statement.setString(2, vaccine_name)
        statement.registerOutParameter(3, Types.INTEGER)
        statement.execute()
        val count = statement.getInt(3)
        statement.close()
        return count > 0
    }

    /**
     * Cancels an appointment in the database.
     *
     * @param appointment_id The ID of the appointment to be cancelled.
     * @return True if the operation was successful, false otherwise.
     */
    override fun cancelAppointment(appointment_id: Int): Boolean {
        val call = "{CALL cancelAppointment(?)}"
        val statement = connection.prepareCall(call)
        appointment_id.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Fetches the details of an appointment from the database.
     *
     * @param appointment_id The ID of the appointment.
     * @return A list of strings representing the details of the appointment.
     */
    override fun fetchAppointmentDetails(appointment_id: Int): List<String> {
        val call = "{CALL fetchAppointmentDetails(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        appointment_id.let { statement.setInt(1, it) }
        statement.registerOutParameter(2, Types.INTEGER)
        statement.registerOutParameter(3, Types.INTEGER)
        statement.registerOutParameter(4, Types.VARCHAR)
        statement.registerOutParameter(5, Types.VARCHAR)
        statement.registerOutParameter(6, Types.INTEGER)
        statement.registerOutParameter(7, Types.VARCHAR)
        statement.registerOutParameter(8, Types.TIMESTAMP)
        statement.registerOutParameter(9, Types.INTEGER)
        statement.registerOutParameter(10, Types.VARCHAR)
        statement.execute()
        val details = listOf(
            statement.getTimestamp(8).toString().substring(0, 10), // Date
            statement.getTimestamp(8).toString().substring(11, 16), // Time
            statement.getString(4), // Name
            statement.getInt(6).toString(), // Age
            statement.getInt(9).toString(), // Dose
            statement.getString(5), // Gender
            statement.getString(7), // Vaccine Name
            statement.getString(10).toString() // Status
        )
        statement.close()
        return details
    }

    /**
     * Fetches an appointment by its ID from the database for admin use.
     *
     * @param appointment_id The ID of the appointment.
     * @return A list of strings representing the details of the appointment, or null if the appointment does not exist.
     */
    override fun fetchAppointmentByIdAdmin(appointment_id: Int): List<String>? {
        var appointment: List<String>? = null
    try {
        val call = "{CALL fetchAppointmentByIdAdmin(?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, appointment_id)
        statement.registerOutParameter(2, Types.TIMESTAMP)
        statement.registerOutParameter(3, Types.INTEGER)
        statement.registerOutParameter(4, Types.VARCHAR)
        statement.execute()

        val dateTimestamp = statement.getTimestamp(2)
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
        val date = format.format(dateTimestamp)
        val time = SimpleDateFormat("HH:mm", Locale.GERMANY).format(dateTimestamp)
        val vaccineName = statement.getString(4)
        appointment = listOf(date, time, vaccineName)

        statement.close()
    } catch (e: SQLException) {
        e.printStackTrace()
    }
    return appointment
}

    /**
     * Fetches the date of an appointment for a user from the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @return A list of dates for the user's appointments.
     */
    override fun getAppointmentDate(firebase_user_id: String): List<Date> {
        val call = "{CALL getAppointmentDate(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val resultSet = statement.executeQuery()
        val dates = mutableListOf<Date>()
        while (resultSet.next()) {
            dates.add(resultSet.getDate("date"))
        }
        return dates
    }

    /**
     * Adds a vaccination record for a user in the database.
     *
     * @param firebase_user_id The Firebase user ID of the user.
     * @param vaccine_name The name of the vaccine.
     * @param dose The dose number of the vaccine.
     * @param date The date of the vaccination.
     * @return True if the operation was successful, false otherwise.
     */
    @SuppressLint("SimpleDateFormat")
    override fun addVaccinationRecord(firebase_user_id: String, vaccine_name: String, dose: Int, date: String): Boolean {
        val userIdVaccineIdCall = "{CALL getUserIdAndVaccineId(?,?,?,?)}"
        val statement = connection.prepareCall(userIdVaccineIdCall)
        statement.setString(1, firebase_user_id)
        statement.setString(2, vaccine_name)
        statement.registerOutParameter(3, Types.INTEGER)
        statement.registerOutParameter(4, Types.INTEGER)
        statement.execute()
        val user_id = statement.getInt(3)
        val vaccine_id = statement.getInt(4)

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val utilDate = format.parse(date)
        val sqlTimestamp = Timestamp(utilDate!!.time)

        val appointment = Appointment(
            user_id = user_id,
            vaccine_id = vaccine_id,
            status = status.Completed,
            dose = dose,
            date = sqlTimestamp
        )

        return insertAppointment(appointment)
    }
}