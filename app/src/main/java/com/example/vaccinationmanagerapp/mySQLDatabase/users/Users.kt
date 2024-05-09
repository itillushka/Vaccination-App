package com.example.vaccinationmanagerapp.mySQLDatabase.users

/**
 * Data class representing a User.
 * @property firebase_user_id The Firebase user ID of the user.
 * @property name The name of the user.
 * @property phone_number The phone number of the user.
 * @property age The age of the user.
 * @property gender The gender of the user.
 * @property role The role of the user.
 */
data class Users(
    //val user_id : Int? = null,
    val firebase_user_id : String? = null,
    val name : String? = null,
    val phone_number : String? = null,
    val age : Int? = null,
    val gender : gender? = null,
    val role : role? = null

)
