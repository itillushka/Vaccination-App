package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import kotlinx.coroutines.*

class ManageUserAccountsAdminActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_user_accounts_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userIdEditText = findViewById<EditText>(R.id.editTextText4)
        val deleteUserEditText = findViewById<EditText>(R.id.editTextText5)
        val userRoleSpinner = findViewById<Spinner>(R.id.spinner)
        val applyButton = findViewById<Button>(R.id.button3)

        val imageViewBack = findViewById<ImageView>(R.id.imageViewBack1)
        imageViewBack.setOnClickListener {
            finish()
        }

        val userRoles = arrayOf("READER", "ADMIN")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, userRoles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        userRoleSpinner.adapter = adapter

        applyButton.setOnClickListener {
            val userEmail = userIdEditText.text.toString()
            val deleteUserEmail = deleteUserEditText.text.toString()

            when {
                userEmail.isNotEmpty() && deleteUserEmail.isNotEmpty() -> {
                    Toast.makeText(this, "Error: Both fields are filled. Please fill only one field at a time.", Toast.LENGTH_SHORT).show()
                }
                userEmail.isNotEmpty() -> {
                    val selectedRole = userRoleSpinner.selectedItem.toString()
                    coroutineScope.launch {
                        changeUserRoleByUserId(userEmail, selectedRole)
                    }
                }
                deleteUserEmail.isNotEmpty() -> {
                    coroutineScope.launch {
                        deleteUserByUserId(deleteUserEmail)
                    }
                }
                else -> {
                    Toast.makeText(this, "Error: Both fields are empty. Please fill one field.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun changeUserRoleByUserId(userId: String, role: String) {
        withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val usersDBQueries = UsersDBQueries(connection)
            val result = usersDBQueries.changeUserRoleByUserId(userId, role)
            connection.close()

            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(this@ManageUserAccountsAdminActivity, "User role changed successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageUserAccountsAdminActivity, "Failed to change user role", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun deleteUserByUserId(userId: String) {
        withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val usersDBQueries = UsersDBQueries(connection)
            val result = usersDBQueries.deleteUserByUserId(userId)
            connection.close()

            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(this@ManageUserAccountsAdminActivity, "User deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageUserAccountsAdminActivity, "Failed to delete user", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}