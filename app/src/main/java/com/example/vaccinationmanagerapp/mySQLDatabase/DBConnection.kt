package com.example.vaccinationmanagerapp.mySQLDatabase

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Singleton object for managing database connections.
 *
 * This object provides a method to get a connection to the database.
 * The database connection details are defined as constants.
 */
object DBconnection {
    // Database connection details
    private const val URL ="jdbc:mysql://sql11.freesqldatabase.com:3306/sql11691536?useUnicode=true&characterEncoding=utf-8&serverTimezone=CET"
    private const val USER = "sql11691536"
    private const val PASS = "GrQUHa85ia"
    init {
        Class.forName("com.mysql.jdbc.Driver")
    }

    /**
     * Gets a connection to the database.
     *
     * @return A Connection object for interacting with the database.
     * @throws RuntimeException if there is an error connecting to the database.
     */
    fun getConnection(): Connection {
        try {
            return DriverManager.getConnection(URL, USER, PASS)
        } catch (ex: SQLException) {
            throw RuntimeException("Error connecting to the database", ex)
        }
    }

    /**
     * Main function to test the database connection.
     *
     * This function gets a connection to the database and then closes it.
     * If there is an error, it prints the stack trace.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // Getting a connection
            val conn = getConnection()
            // Closing the connection
            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}