package com.example.vaccinationmanagerapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Main activity for the admin user of the application.
 *
 * This activity allows the admin user to manage user appointments, user accounts, and vaccines.
 */
class AdminMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonManageUserAppointments = findViewById<Button>(R.id.buttonManageUserAppointments)
        val buttonManageUserAccount = findViewById<Button>(R.id.buttonManageUserAccount)
        val buttonManageVaccines = findViewById<Button>(R.id.buttonManageVaccines)

        buttonManageUserAppointments.setOnClickListener {
            val intent = Intent(this, ManageAppointmentsAdminActivity::class.java)
            startActivity(intent)
        }

        buttonManageUserAccount.setOnClickListener {
            val intent = Intent(this, ManageUserAccountsAdminActivity::class.java)
            startActivity(intent)
        }

        buttonManageVaccines.setOnClickListener {
            val intent = Intent(this, ManageVaccinesAdminActivity::class.java)
            startActivity(intent)
        }
    }
}