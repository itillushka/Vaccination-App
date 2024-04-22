package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.adapters.VaccineTypeAdapter
import com.example.vaccinationmanagerapp.decorators.SpaceItemDecoration
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.VaccineDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class AddRecordActivity : AppCompatActivity() {
    private var vaccineTypes = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButtonVaccine = findViewById<ImageButton>(R.id.backButtonVaccine)
        backButtonVaccine.setOnClickListener {
            finish()
        }
        val calendar = findViewById<CalendarView>(R.id.calendarView1)
        val dateView = findViewById<TextView>(R.id.dateView1)
        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val currentDate = Calendar.getInstance()

            if (selectedDate.after(currentDate)) {
                Toast.makeText(this, "You cannot add record on a future date", Toast.LENGTH_SHORT).show()
            } else {
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

                dateView.text = date
            }
        }
        val space = resources.getDimensionPixelSize(R.dimen.item_space)

        runBlocking { launch(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val dbQueries = VaccineDBQueries(connection)

            vaccineTypes = dbQueries.getAllVaccines().toMutableList()
            println(vaccineTypes)

            connection.close()}}

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }
        val vaccineAvailability = vaccineTypes.map {
            vaccine -> Pair(vaccine, true)
        }
        val vaccineTypeAdapter = VaccineTypeAdapter(vaccineAvailability)
        val vaccineTypeRecyclerView = findViewById<RecyclerView>(R.id.vaccineTypeRecyclerView1)
        vaccineTypeRecyclerView.adapter = vaccineTypeAdapter
        vaccineTypeRecyclerView.addItemDecoration(SpaceItemDecoration(space))

        val doseSpinner: Spinner = findViewById(R.id.doseSpinner)
        val doseAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.dose,
            android.R.layout.simple_spinner_item
        )
        doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        doseSpinner.adapter = doseAdapter

        val confirmAppointmentButton = findViewById<Button>(R.id.confirmButton)
        confirmAppointmentButton.setOnClickListener {
            val selectedVaccineType = vaccineTypeAdapter.getSelectedVaccineType()
            var selectedDate = dateView.text.toString() + " 00:00"
            var dose = doseSpinner.selectedItem.toString().toInt()

            runBlocking { launch(Dispatchers.IO) {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)

                val result = dbQueries.addVaccinationRecord(firebaseUserId, selectedVaccineType!!, dose, selectedDate)

                connection.close()

                if (result) {
                    // If the appointment is successfully created, finish the activity
                    finish()
                } else {
                    // Handle the error
                }
            }}
        }
    }
}