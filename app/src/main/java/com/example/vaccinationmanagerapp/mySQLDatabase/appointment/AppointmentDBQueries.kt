package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

import java.sql.Connection
import java.sql.Date

class AppointmentDBQueries(private val connection: Connection) : AppointmentDAO {

    override fun insertAppointment(appointment: Appointment): Boolean {
        val call = "{CALL insertAppointment(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        appointment.user_id?.let { statement.setInt(1, it) }
        appointment.vaccine_id?.let { statement.setInt(2, it) }
        statement.setString(3, appointment.status?.name ?: "Scheduled")
        appointment.dose?.let { statement.setInt(4, it) }
        statement.setDate(5, appointment.date)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun deleteAppointment(appointment: Appointment): Boolean {
        val call = "{CALL deleteAppointment(?)}"
        val statement = connection.prepareCall(call)
        appointment.appointment_id?.let { statement.setInt(1, it) }
        val result = !statement.execute()
        statement.close()
        return result
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