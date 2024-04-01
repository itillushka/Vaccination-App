package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

interface AppointmentDAO {

    fun insertAppointment(appointment: Appointment): Boolean

    fun deleteAppointment(appointment: Appointment): Boolean
}