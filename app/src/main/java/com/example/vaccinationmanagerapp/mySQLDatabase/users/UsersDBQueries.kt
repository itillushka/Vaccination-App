package com.example.vaccinationmanagerapp.mySQLDatabase.users

import java.sql.Connection

class UsersDBQueries(private val connection: Connection) : UsersDAO {
    override fun insertUser(user: Users): Boolean {
        val call = "{CALL insertUsers(?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, user.firebase_user_id)
        statement.setString(2, user.name)
        statement.setString(3, user.phone_number)
        user.age?.let { statement.setInt(4, it) }
        statement.setString(5, user.gender?.name ?: "Other")
        statement.setString(6, user.role?.name ?: "READER")
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun insertRegisterUser(user: Users): Boolean {
        val call = "{CALL insertRegisterUsers(?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, user.firebase_user_id)
        statement.setString(2, user.name)
        val result = !statement.execute()
        statement.close()
        return result
    }


}