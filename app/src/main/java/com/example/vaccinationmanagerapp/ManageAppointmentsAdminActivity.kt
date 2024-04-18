package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManageAppointmentsAdminActivity : AppCompatActivity() {
    suspend fun fetchAppointmentById(id: Int){
        withContext(Dispatchers.IO) {
            // Getting connection using DBConnection class
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)

            val appointment = dbQueries.fetchAppointmentByIdAdmin(id)

            withContext(Dispatchers.Main) {
                if (appointment != null) {
                    // Get the other EditTexts
                    val editTextDate = findViewById<EditText>(R.id.editTextDate)
                    val editTextTime = findViewById<EditText>(R.id.editTextTime)
                    val editTextVaccineName = findViewById<EditText>(R.id.editTextText6)

                    // Set the hints of the other EditTexts
                    editTextDate.hint = appointment[0]
                    editTextTime.hint = appointment[1]
                    editTextVaccineName.hint = appointment[2]
                } else {
                    // Handle the case where the appointment is null
                    Toast.makeText(applicationContext, "No appointment found with this id.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_appointments_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        val imageViewBack = findViewById<ImageView>(R.id.imageViewBack)
        imageViewBack.setOnClickListener {
            finish()
        }

        // Get the EditText for the id
        val editTextId = findViewById<EditText>(R.id.editTextNumber)

        // Get the id from the EditText
        val buttonSearch = findViewById<Button>(R.id.buttonSearch)
        buttonSearch.setOnClickListener {
            val idText = editTextId.text.toString()
            if (idText.isNotEmpty()) {
                val id = idText.toInt()
                lifecycleScope.launch { fetchAppointmentById(id) }
            } else {
                Toast.makeText(this, "Please enter an id", Toast.LENGTH_SHORT).show()
            }
        }

    }
}