package com.example.vaccinationmanagerapp.mySQLDatabase.users

data class Users(
    //val user_id : Int? = null,
    val firebase_user_id : String? = null,
    val name : String? = null,
    val phone_number : String? = null,
    val age : Int? = null,
    val gender : gender? = null,
    val role : role? = null

)
