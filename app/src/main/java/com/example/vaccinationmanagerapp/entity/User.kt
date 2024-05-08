package com.example.vaccinationmanagerapp.entity
/**
 * A data class that represents a user in the database.
 *
 * @property name The name of the user.
 * @property email The email of the user.
 * @property password The password of the user.
 *
 * @constructor Creates an instance of User with the provided properties.
 */
data class User(
    var name: String,
    var email: String,
    var password: String
)
