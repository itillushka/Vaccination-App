package com.example.vaccinationmanagerapp.mySQLDatabase.users

import java.sql.Connection

/**
 * Class for User Database Queries.
 * This class implements the UsersDAO interface and defines the methods for interacting with the User data in the database.
 * @property connection The connection to the database.
 */
class UsersDBQueries(private val connection: Connection) : UsersDAO {

    /**
     * Inserts a new user into the database.
     * @param user The user to be inserted.
     * @return Boolean indicating success or failure.
     */
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

    /**
     * Inserts a new registered user into the database.
     * @param user The user to be inserted.
     * @return Boolean indicating success or failure.
     */
    override fun insertRegisterUser(user: Users): Boolean {
        val call = "{CALL insertRegisterUsers(?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, user.firebase_user_id)
        statement.setString(2, user.name)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Updates a user in the database.
     * @param phone_number The new phone number of the user.
     * @param age The new age of the user.
     * @param gender The new gender of the user.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return Boolean indicating success or failure.
     */
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

    /**
     * Fetches the data of a user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return List of user data.
     */
    override fun fetchUserData(firebase_user_id: String): List<String> {
        val call = "{CALL fetchUserData(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val resultSet = statement.executeQuery()
        val result = mutableListOf<String>()
        while (resultSet.next()) {
            val phoneNumber = resultSet.getString("phone_number") ?: ""
            val age = resultSet.getInt("age").toString() ?: ""
            val gender = resultSet.getString("gender") ?: ""
            result.add(phoneNumber)
            result.add(age)
            result.add(gender)
        }
        resultSet.close()
        statement.close()
        return result
    }

    /**
     * Fetches the role of a user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return String representing the user's role.
     */
    override fun fetchUserRole(firebase_user_id: String): String {
    val call = "{CALL fetchUserRole(?)}"
    val statement = connection.prepareCall(call)
    statement.setString(1, firebase_user_id)
    val resultSet = statement.executeQuery()
    var role = ""
    if (resultSet.next()) {
        role = resultSet.getString("role")
    }
    resultSet.close()
    statement.close()
    return role
    }

    /**
     * Changes the role of a user in the database.
     * @param userId The ID of the user.
     * @param role The new role of the user.
     * @return Boolean indicating success or failure.
     */
    override fun changeUserRoleByUserId(userId: String, role: String): Boolean {
    val call = "{CALL changeUserRoleByUserId(?, ?)}"
    val statement = connection.prepareCall(call)
    statement.setInt(1, userId.toInt())
    statement.setString(2, role)
    val result = !statement.execute()
    statement.close()
    return result
    }

    /**
     * Deletes a user from the database.
     * @param userId The ID of the user.
     * @return Boolean indicating success or failure.
     */
    override fun deleteUserByUserId(userId: String): Boolean {
        val call = "{CALL deleteUserByUserId(?)}"
        val statement = connection.prepareCall(call)
        statement.setInt(1, userId.toInt())
        val result = !statement.execute()
        statement.close()
        return result
    }


}