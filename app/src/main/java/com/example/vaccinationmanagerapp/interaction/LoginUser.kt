package com.example.vaccinationmanagerapp.interaction

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.example.vaccinationmanagerapp.AdminMainActivity
import com.example.vaccinationmanagerapp.MainActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginUser(private val activity: Activity) {
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    fun loginUser(email: String, password: String) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // User logged in successfully
                Toast.makeText(activity, "Logged in successfully", Toast.LENGTH_SHORT).show()
                val firebaseUser = auth.currentUser
                var firebaseUserId : String = ""

                if (firebaseUser != null) {
                    firebaseUserId = firebaseUser.uid
                }

                // Fetching the user role from the database
                var userRole = ""
                runBlocking { launch(Dispatchers.IO) {
                    // Getting connection using DBConnection class
                    val connection = DBconnection.getConnection()
                    val dbQueries = UsersDBQueries(connection)
                    userRole = dbQueries.fetchUserRole(firebaseUserId)
                    connection.close() // Closing the database connection
                } }

                val intent = if (userRole == "ADMIN") {
                    Intent(activity, AdminMainActivity::class.java)
                } else {
                    Intent(activity, MainActivity::class.java)
                }
                activity.startActivity(intent)
            } else {
                // Login failed
                Toast.makeText(activity, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
}

    fun googleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // User logged in successfully
                    Toast.makeText(activity, "Logged in successfully with Google", Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    // Login failed
                    Toast.makeText(activity, "Google login failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}