package com.example.vaccinationmanagerapp.mySQLDatabase.users

interface UsersDAO {
    fun insertUser(user: Users): Boolean
    fun insertRegisterUser(user: Users): Boolean
}