package com.example.vaccinationmanagerapp.mySQLDatabase.users

/**
 * Interface for User Data Access Object.
 * This interface defines the methods for interacting with the User data in the database.
 */
interface UsersDAO {

    /**
     * Inserts a new user into the database.
     * @param user The user to be inserted.
     * @return Boolean indicating success or failure.
     */
    fun insertUser(user: Users): Boolean

    /**
     * Inserts a new registered user into the database.
     * @param user The user to be inserted.
     * @return Boolean indicating success or failure.
     */
    fun insertRegisterUser(user: Users): Boolean

    /**
     * Updates a user in the database.
     * @param phone_number The new phone number of the user.
     * @param age The new age of the user.
     * @param gender The new gender of the user.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return Boolean indicating success or failure.
     */
    fun updateUser(phone_number: String, age: Int, gender: gender, firebase_user_id : String): Boolean

    /**
     * Fetches the data of a user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return List of user data.
     */
    fun fetchUserData(firebase_user_id: String): List<String>

    /**
     * Fetches the role of a user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return String representing the user's role.
     */
    fun fetchUserRole(firebase_user_id: String): String

    /**
     * Changes the role of a user in the database.
     * @param userId The ID of the user.
     * @param role The new role of the user.
     * @return Boolean indicating success or failure.
     */
    fun changeUserRoleByUserId(userId: String, role: String): Boolean

    /**
     * Deletes a user from the database.
     * @param userId The ID of the user.
     * @return Boolean indicating success or failure.
     */
    fun deleteUserByUserId(userId: String): Boolean
}