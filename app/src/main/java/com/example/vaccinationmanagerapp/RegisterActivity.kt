package com.example.vaccinationmanagerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationmanagerapp.interaction.RegisterUser

/**
 * This activity provides a registration form for new users.
 * It asks for the user's name, email, and password.
 * When the user clicks the register button, the entered details are used to create a new user account.
 * If the user already has an account, they can navigate to the login activity by clicking on the text view at the bottom.
 */
class RegisterActivity : AppCompatActivity() {
    private lateinit var registerUser: RegisterUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        registerUser = RegisterUser(this)

        val nameEditText = findViewById<EditText>(R.id.editTextNameRegister)
        val emailEditText = findViewById<EditText>(R.id.editTextEmailAddressRegister)
        val passwordEditText = findViewById<EditText>(R.id.editTextPasswordRegister)
        val registerButton = findViewById<Button>(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            registerUser.registerUser(name, email, password)
        }

        val AccountLoginClick = findViewById<TextView>(R.id.textViewAccountLoginClick)
        AccountLoginClick.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}