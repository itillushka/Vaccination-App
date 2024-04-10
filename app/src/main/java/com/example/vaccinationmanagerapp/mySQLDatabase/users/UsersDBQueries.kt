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

    override fun updateUser(
        phone_number: String,
        age: Int,
        gender: gender,
        firebase_user_id: String
    ): Boolean {
        val call = "{CALL updateUsers(?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        statement.setString(2, phone_number)
        statement.setInt(3, age)
        statement.setString(4, gender.name)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun fetchUserData(firebase_user_id: String): List<String> {
        val call = "{CALL fetchUserData(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val resultSet = statement.executeQuery()
        val result = mutableListOf<String>()
        while (resultSet.next()) {
            val phoneNumber = resultSet.getString("phone_number")
            val age = resultSet.getInt("age").toString()
            val gender = resultSet.getString("gender")
            result.add(phoneNumber)
            result.add(age)
            result.add(gender)
        }
        resultSet.close()
        statement.close()
        return result
    }


}