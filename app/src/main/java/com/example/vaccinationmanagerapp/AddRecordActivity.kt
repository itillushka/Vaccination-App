package com.example.vaccinationmanagerapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.adapters.VaccineTypeAdapter
import com.example.vaccinationmanagerapp.decorators.SpaceItemDecoration

class AddRecordActivity : AppCompatActivity() {
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
            val date = "$dayOfMonth-${month + 1}-$year"

            dateView.text = date
        }
        val space = resources.getDimensionPixelSize(R.dimen.item_space)

        val vaccineTypes = listOf("Polio", "Rotavirus", "Chickenpox", "MMR","Meningococcal", "Influenza", "DTaP", "Hepatitis A", "HPV")
        val vaccineTypeAdapter = VaccineTypeAdapter(vaccineTypes)
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
    }
}