package com.example.vaccinationmanagerapp.mySQLDatabase.appointment

interface AppointmentDAO {

    fun insertAppointment(appointment: Appointment): Boolean

    fun deleteAppointment(appointment_id: Int): Boolean

    fun createAppointment(vaccine_name: String, date: String, firebase_user_id: String): Boolean

    fun cancelAppointment(appointment_id: Int): Boolean

    fun fetchAppointmentDetails(appointment_id: Int) : List<String>
}