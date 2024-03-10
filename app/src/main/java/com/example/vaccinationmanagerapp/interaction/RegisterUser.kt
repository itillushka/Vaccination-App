package com.example.vaccinationmanagerapp.interaction

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterUser(private val context: Context) {
    private lateinit var auth: FirebaseAuth

    fun registerUser(name: String, email: String, password: String) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // User profile updated successfully
                                Toast.makeText(context, "Registered successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Registration failed
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}