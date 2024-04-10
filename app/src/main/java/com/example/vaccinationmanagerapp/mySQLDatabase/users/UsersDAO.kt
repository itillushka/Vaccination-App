package com.example.vaccinationmanagerapp.mySQLDatabase.users

interface UsersDAO {
    fun insertUser(user: Users): Boolean
    fun insertRegisterUser(user: Users): Boolean
    fun updateUser(phone_number: String, age: Int, gender: gender, firebase_user_id : String): Boolean
    fun fetchUserData(firebase_user_id: String): List<String>
}