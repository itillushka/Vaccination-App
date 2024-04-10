package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.Vaccine
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.VaccineDBQueries
import kotlinx.coroutines.*

class ManageVaccinesAdminActivity : AppCompatActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_vaccines_admin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val switch = findViewById<Switch>(R.id.switch1)
        val vaccineNameEditText = findViewById<EditText>(R.id.editTextText)
        val numberOfDosesEditText = findViewById<EditText>(R.id.editTextText2)
        val timeBetweenDosesEditText = findViewById<EditText>(R.id.editTextText3)
        val applyButton = findViewById<Button>(R.id.button2)

        switch.setOnCheckedChangeListener { _, isChecked ->
            numberOfDosesEditText.isEnabled = !isChecked
            timeBetweenDosesEditText.isEnabled = !isChecked
        }

        applyButton.setOnClickListener {
            if (switch.isChecked) {
                val vaccineName = vaccineNameEditText.text.toString()
                coroutineScope.launch {
                    deleteVaccineByName(vaccineName)
                }
            } else {
                val vaccineName = vaccineNameEditText.text.toString()
                val numberOfDoses = numberOfDosesEditText.text.toString().toInt()
                val timeBetweenDoses = timeBetweenDosesEditText.text.toString().toInt()
                val vaccine = Vaccine(0, vaccineName, numberOfDoses, timeBetweenDoses)
                coroutineScope.launch {
                    insertVaccine(vaccine)
                }
            }
        }
    }

    private suspend fun deleteVaccineByName(vaccineName: String) {
        withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val vaccineDBQueries = VaccineDBQueries(connection)
            val result = vaccineDBQueries.deleteVaccineByName(vaccineName)
            connection.close()

            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(this@ManageVaccinesAdminActivity, "Vaccine deleted successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageVaccinesAdminActivity, "Failed to delete vaccine", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun insertVaccine(vaccine: Vaccine) {
        withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val vaccineDBQueries = VaccineDBQueries(connection)
            val result = vaccineDBQueries.insertVaccine(vaccine)
            connection.close()

            withContext(Dispatchers.Main) {
                if (result) {
                    Toast.makeText(this@ManageVaccinesAdminActivity, "Vaccine added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ManageVaccinesAdminActivity, "Failed to add vaccine", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}