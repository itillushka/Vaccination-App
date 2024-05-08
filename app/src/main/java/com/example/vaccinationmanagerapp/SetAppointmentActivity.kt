package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.adapters.TimeSlotAdapter
import com.example.vaccinationmanagerapp.adapters.VaccineTypeAdapter
import com.example.vaccinationmanagerapp.decorators.SpaceItemDecoration
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.Appointment
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.Notifications
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.vaccine.VaccineDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.sql.Timestamp
import java.util.Calendar

/**
 * This activity allows the user to set an appointment for vaccination.
 * It provides a calendar for the user to select a date and a list of time slots to choose from.
 * It also provides a list of available vaccines for the user to select.
 * Once the user has selected a date, time slot, and vaccine, they can confirm the appointment.
 * The appointment details are then saved in the database.
 */
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
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)

            val currentDate = Calendar.getInstance()

            if (selectedDate.before(currentDate)) {
                Toast.makeText(this, "You cannot set an appointment on a past date", Toast.LENGTH_SHORT).show()
            } else {
                val date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)

                dateView.text = date
            }
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

                if (result) {
                    // If the appointment is successfully created, finish the activity
                    val notificationService = MyFirebaseMessagingService()
                    notificationService.generateNotification(applicationContext,"Appointment Reminder", "Your appointment is created!")

                    // Insert the notification into the database
                    val notificationDBQueries = NotificationsDBQueries(connection)
                    val notification = Notifications(
                        firebase_user_id = firebaseUserId,
                        date_sent = Timestamp(System.currentTimeMillis()),
                        title = "Appointment Reminder",
                        description = "Your appointment is created!"
                    )
                    notificationDBQueries.insertNotifications(notification)
                    finish()
                } else {
                    // Handle the error
                }

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