package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.adapters.TimeSlotAdapter
import com.example.vaccinationmanagerapp.adapters.VaccineTypeAdapter
import com.example.vaccinationmanagerapp.decorators.SpaceItemDecoration
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.Appointment
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.VaccineDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date


class SetAppointmentActivity : AppCompatActivity() {

    private var vaccineTypes = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_set_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
        //implementation of calendar view
        val calendar = findViewById<CalendarView>(R.id.calendarView)
        val dateView = findViewById<TextView>(R.id.dateView)
        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            // Store the value of date with format in String type Variable
            // Add 1 in month because month index is start with 0
            val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

            dateView.text = date
        }
        //implementation of time slots recycler view
        val timeSlots = listOf(
            Pair("12:30", true),
            Pair("1:00", false),
            Pair("1:30", true),
            Pair("2:00", true),
            Pair("2:30", false),
            Pair("3:00", true),
            Pair("3:30", true)
        )
        val timeSlotAdapter = TimeSlotAdapter(timeSlots)
        val timeSlotsRecyclerView = findViewById<RecyclerView>(R.id.timeSlotsRecyclerView)
        timeSlotsRecyclerView.adapter = timeSlotAdapter
        //adding space between items
        val space = resources.getDimensionPixelSize(R.dimen.item_space)
        timeSlotsRecyclerView.addItemDecoration(SpaceItemDecoration(space))

        //implementation of vaccine type recycler view
        //val vaccineTypes = listOf("Polio", "Rotavirus", "Chickenpox", "MMR","Meningococcal", "Influenza", "DTaP", "Hepatitis A", "HPV")
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

        val vaccineAvailability = runBlocking {
            val availabilityDeferred = async(Dispatchers.IO) {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)
                val availability = vaccineTypes.map { vaccine ->
                    val hasActiveAppointment = dbQueries.hasActiveAppointment(firebaseUserId, vaccine)
                    Pair(vaccine, !hasActiveAppointment)
                }
                connection.close()
                availability
            }
            availabilityDeferred.await()
        }

        val vaccineTypeAdapter = VaccineTypeAdapter(vaccineAvailability)
        val vaccineTypeRecyclerView = findViewById<RecyclerView>(R.id.vaccineTypeRecyclerView)
        vaccineTypeRecyclerView.adapter = vaccineTypeAdapter
        vaccineTypeRecyclerView.addItemDecoration(SpaceItemDecoration(space))

        val confirmAppointmentButton = findViewById<Button>(R.id.confirmAppointmentButton)
        confirmAppointmentButton.setOnClickListener {
            // Get selected vaccine type, date, and time
            val selectedVaccineType = vaccineTypeAdapter.getSelectedVaccineType()
            var selectedDate = dateView.text.toString()
            val selectedTimeSlot = timeSlotAdapter.getSelectedTimeSlot()

            if (selectedTimeSlot != null) {
                selectedDate = selectedDate + " " + selectedTimeSlot.first
            }

            // Save the new appointment in the database
            runBlocking { launch(Dispatchers.IO) {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)

                val result = dbQueries.createAppointment(selectedVaccineType!!, selectedDate, firebaseUserId)

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