package com.example.vaccinationmanagerapp.mySQLDatabase

import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.Users
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.gender
import com.example.vaccinationmanagerapp.mySQLDatabase.users.role
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.VaccineDBQueries

/*fun main() {
    try {
        // Getting connection using DBConnection class
        val connection = DBconnection.getConnection()
        val dbQueries = UsersDBQueries(connection)

        println("Testing insertUser():")
        val newUser = Users("1233456", "Illia", "099099099",
            19, gender.Male, role.READER)
        println("Insertion successful:${dbQueries.insertUser(newUser)}")
        connection.close() // Closing the database connection
    } catch (e: Exception) {
        e.printStackTrace()
    }
}*/

/*fun main(){
    val firebaseUserId = "someID"

    // Getting connection using DBConnection class
    val connection = DBconnection.getConnection()
    val dbQueries = UsersDBQueries(connection)

    // Creating a new user with firebase_user_id and name
    val newUser = Users(firebaseUserId, "Maryna")

    // Inserting the new user into the database
    dbQueries.insertRegisterUser(newUser)

    connection.close()
}*/



fun testFetchAppointmentDetails() {
    // Initialize the database connection and queries
    val connection = DBconnection.getConnection()
    val dbQueries = AppointmentDBQueries(connection)

    // Use a known appointment_id for testing
    val appointment_id = 2

    // Call the fetchAppointmentDetails function
    val details = dbQueries.fetchAppointmentDetails(appointment_id)

    // Print the details
    println(details)

    // Close the connection
    connection.close()
}

fun main(){

// Call the test function
testFetchAppointmentDetails()}