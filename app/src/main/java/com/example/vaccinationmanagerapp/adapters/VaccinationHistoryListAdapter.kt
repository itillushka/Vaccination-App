package com.example.vaccinationmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.models.VaccinationRecord
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class RecordItem(
    val appointment_id: Int,
    val vaccine_name: String,
    val date: String,
    val dose: Int,
    var status: status
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
        holder.nextDose.text = "Next Dose: TODO"

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

    override fun getItemCount() = records.size
}