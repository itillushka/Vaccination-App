package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import android.util.Log
import java.sql.Connection
import java.sql.Date
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentDBQueries(private val connection: Connection) : AppointmentDAO {

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

    override fun deleteAppointment(appointment: Appointment): Boolean {
        val call = "{CALL deleteAppointment(?)}"
        val statement = connection.prepareCall(call)
        appointment.appointment_id?.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }
    override fun createAppointment(vaccine_name: String, date: String, firebase_user_id: String): Boolean {
        // Extract user_id from Appointment table using firebase_user_id
        val userIdQuery = "SELECT user_id FROM Users WHERE firebase_user_id = ?"
        val userIdStatement = connection.prepareStatement(userIdQuery)
        userIdStatement.setString(1, firebase_user_id)
        val userIdResult = userIdStatement.executeQuery()
        val user_id = if (userIdResult.next()) userIdResult.getInt("user_id") else null
        userIdStatement.close()

        // Extract vaccine_id using vaccine_name
        val vaccineIdQuery = "SELECT vaccine_id FROM Vaccine WHERE vaccine_name = ?"
        val vaccineIdStatement = connection.prepareStatement(vaccineIdQuery)
        vaccineIdStatement.setString(1, vaccine_name)
        val vaccineIdResult = vaccineIdStatement.executeQuery()
        val vaccine_id = if (vaccineIdResult.next()) vaccineIdResult.getInt("vaccine_id") else null
        vaccineIdStatement.close()

        // Check for existing appointment with same user_id, vaccine_id and status = 'Completed'
        val doseQuery = "SELECT dose FROM Appointment WHERE user_id = ? AND vaccine_id = ? AND status = 'Completed'"
        val doseStatement = connection.prepareStatement(doseQuery)
        user_id?.let { doseStatement.setInt(1, it) }
        vaccine_id?.let { doseStatement.setInt(2, it) }
        val doseResult = doseStatement.executeQuery()
        val dose = if (doseResult.next()) doseResult.getInt("dose") + 1 else 1
        doseStatement.close()



        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val utilDate = format.parse(date)
        val sqlTimestamp = Timestamp(utilDate.time)

        Log.d("createAppointment", "user_id: $user_id")
        Log.d("createAppointment", "vaccine_id: $vaccine_id")
        Log.d("createAppointment", "dose: $dose")
        Log.d("createAppointment", "dose: $sqlTimestamp")

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
    fun getAppointmentsByUser(firebase_user_id: String): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        val query = """
        SELECT Appointment.*, Vaccine.vaccine_name 
        FROM Appointment 
        INNER JOIN Users ON Appointment.user_id = Users.user_id 
        INNER JOIN Vaccine ON Appointment.vaccine_id = Vaccine.vaccine_id 
        WHERE Users.firebase_user_id = ?
        """
        val statement = connection.prepareStatement(query)
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
    fun hasActiveAppointment(firebase_user_id: String, vaccine_name: String): Boolean {
        val query = """
            SELECT COUNT(*) as count
            FROM Appointment
            INNER JOIN Users ON Appointment.user_id = Users.user_id
            INNER JOIN Vaccine ON Appointment.vaccine_id = Vaccine.vaccine_id
            WHERE Users.firebase_user_id = ? AND Vaccine.vaccine_name = ? AND Appointment.status = 'Scheduled'
        """
        val statement = connection.prepareStatement(query)
        statement.setString(1, firebase_user_id)
        statement.setString(2, vaccine_name)
        val result = statement.executeQuery()
        val count = if (result.next()) result.getInt("count") else 0
        statement.close()
        return count > 0
    }
    override fun cancelAppointment(appointment_id: Int): Boolean {
        val call = "{CALL cancelAppointment(?)}"
        val statement = connection.prepareCall(call)
        appointment_id.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun fetchAppointmentByIdAdmin(appointment_id: Int): List<String>? {
        var appointment: List<String>? = null
    try {
        val call = "{CALL fetchAppointmentByIdAdmin(?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, appointment_id)
        statement.registerOutParameter(2, java.sql.Types.TIMESTAMP)
        statement.registerOutParameter(3, java.sql.Types.INTEGER)
        statement.registerOutParameter(4, java.sql.Types.VARCHAR)
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

    fun getAppointmentDate(firebase_user_id: String): Date? {
        val call = "{CALL getAppointmentDate(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getDate("date")
        } else {
            null
        }
    }
}