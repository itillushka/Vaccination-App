package com.example.vaccinationmanagerapp.interaction

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.vaccinationmanagerapp.LoginActivity
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.users.Users
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Class for handling user registration.
 *
 * @property context The context in which this class is being used.
 */
class RegisterUser(private val context: Context) {
    private lateinit var auth: FirebaseAuth

    /**
     * Inserts the Firebase user ID and name into the database.
     *
     * @param firebaseUserId The Firebase user ID of the user.
     * @param name The name of the user.
     */
    suspend fun insertIDAndName(firebaseUserId : String?, name : String){
        withContext(Dispatchers.IO) {
            // Getting connection using DBConnection class
            val connection = DBconnection.getConnection()
            val dbQueries = UsersDBQueries(connection)

            // Creating a new user with firebase_user_id and name
            val newUser = Users(firebaseUserId, name)

            // Inserting the new user into the database
            dbQueries.insertRegisterUser(newUser)

            connection.close() // Closing the database connection
        }
    }

    /**
     * Registers a new user with the specified name, email, and password.
     *
     * @param name The name of the user.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun registerUser(name: String, email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Add user id and name to mySQL
                    val firebaseUser = auth.currentUser
                    val firebaseUserId = firebaseUser?.uid

                    // Perform the database connection operation on a background thread
                    runBlocking {
                        launch(Dispatchers.IO) {
                            insertIDAndName(firebaseUserId, name)
                        }
                    }
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User profile updated successfully
                                Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, LoginActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                } else {
                    // Registration failed
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}