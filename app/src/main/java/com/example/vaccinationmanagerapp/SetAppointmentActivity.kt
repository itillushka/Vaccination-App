package com.example.vaccinationmanagerapp

import android.os.Bundle
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


class SetAppointmentActivity : AppCompatActivity() {
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
            val date = "$dayOfMonth-${month + 1}-$year"

            dateView.text = date
        }
        //implementation of time slots recycler view
        val timeSlots = listOf(
            Pair("12:30pm", true),
            Pair("1:00pm", false),
            Pair("1:30pm", true),
            Pair("2:00pm", true),
            Pair("2:30pm", false),
            Pair("3:00pm", true),
            Pair("3:30pm", true)
        )
        val timeSlotAdapter = TimeSlotAdapter(timeSlots)
        val timeSlotsRecyclerView = findViewById<RecyclerView>(R.id.timeSlotsRecyclerView)
        timeSlotsRecyclerView.adapter = timeSlotAdapter
        //adding space between items
        val space = resources.getDimensionPixelSize(R.dimen.item_space)
        timeSlotsRecyclerView.addItemDecoration(SpaceItemDecoration(space))

        //implementation of vaccine type recycler view
        val vaccineTypes = listOf("Polio", "Rotavirus", "Chickenpox", "MMR","Meningococcal", "Influenza", "DTaP", "Hepatitis A", "HPV")
        val vaccineTypeAdapter = VaccineTypeAdapter(vaccineTypes)
        val vaccineTypeRecyclerView = findViewById<RecyclerView>(R.id.vaccineTypeRecyclerView)
        vaccineTypeRecyclerView.adapter = vaccineTypeAdapter
        vaccineTypeRecyclerView.addItemDecoration(SpaceItemDecoration(space))

    }

}