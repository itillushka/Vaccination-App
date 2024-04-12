package com.example.vaccinationmanagerapp.mySQLDatabase

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

fun main(){
    val connection = DBconnection.getConnection()
    val dbQueries = VaccineDBQueries(connection)

    // Inserting the new user into the database
    dbQueries.getAllVaccines().forEach { println(it) }

    connection.close()
}