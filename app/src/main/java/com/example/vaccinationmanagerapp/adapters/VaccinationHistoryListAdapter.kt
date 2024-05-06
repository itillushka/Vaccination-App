package com.example.vaccinationmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class RecordItem(
    val appointment_id: Int,
    val vaccine_name: String,
    val date: String,
    val dose: Int,
    var status: status,
    val number_of_doses: Int,
    var time_between_doses: Int
)

class VaccinationHistoryListAdapter(private val records: List<RecordItem>) :
    RecyclerView.Adapter<VaccinationHistoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vaccineName: TextView = view.findViewById(R.id.vacccineName)
        val doseText: TextView = view.findViewById(R.id.doseText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val nextDose: TextView = view.findViewById(R.id.nextDose)
        val deleteRecord: Button = view.findViewById(R.id.deleteRecordButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vacccination_history_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = records[position]
        holder.vaccineName.text = currentItem.vaccine_name
        holder.doseText.text = "Dose: " + currentItem.dose.toString()
        holder.dateText.text = currentItem.date.substring(0, 10)
        holder.nextDose.text = calculateNextDose(currentItem.date, currentItem.time_between_doses, currentItem.dose, currentItem.number_of_doses)

        holder.deleteRecord.setOnClickListener {
            val appointmentId = currentItem.appointment_id

            CoroutineScope(Dispatchers.IO).launch {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)

                val result = dbQueries.deleteAppointment(appointmentId)

                connection.close()

                if (result) {
                    withContext(Dispatchers.Main) {
                        holder.deleteRecord.setTextColor(R.color.gray)
                        holder.deleteRecord.setBackgroundResource(R.drawable.unavailable_button)
                        holder.deleteRecord.setText("Deleted")
                    }
                } else {
                    // Handle the error
                }
            }
        }
    }
    fun calculateNextDose(lastVaccinationDate: String, timeBetweenDoses: Int, dose: Int, numberOfDoses: Int): String {
        if (dose >= numberOfDoses) {
            return "No more doses needed"
        }

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = format.parse(lastVaccinationDate)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, timeBetweenDoses)

        val currentDate = Calendar.getInstance()
        val daysUntilNextDose = ((calendar.time.time - currentDate.time.time) / (1000 * 60 * 60 * 24)).toInt()

        return if (daysUntilNextDose > 0) {
            "Next Dose: in $daysUntilNextDose days"
        } else {
            "Next Dose: As soon as possible"
        }
    }

    override fun getItemCount() = records.size
}