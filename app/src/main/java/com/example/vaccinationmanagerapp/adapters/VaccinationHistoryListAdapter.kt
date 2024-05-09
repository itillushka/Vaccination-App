package com.example.vaccinationmanagerapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.entity.RecordItem
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

/**
 * A RecyclerView.Adapter that is used to display a list of vaccination records in a RecyclerView.
 *
 * @property records The list of records to display in the RecyclerView. Each record is represented as an instance of [RecordItem].
 *
 * @constructor Creates an instance of VaccinationHistoryListAdapter with the provided list of records.
 */
class VaccinationHistoryListAdapter(private val records: List<RecordItem>) :
    RecyclerView.Adapter<VaccinationHistoryListAdapter.ViewHolder>() {
    /**
     * A ViewHolder describes an item view and metadata about its place within the RecyclerView.
     *
     * @property vaccineName The TextView that displays the name of the vaccine for the record.
     * @property doseText The TextView that displays the dose number of the record.
     * @property dateText The TextView that displays the date of the record.
     * @property nextDose The TextView that displays the date of the next dose.
     * @property deleteRecord The Button that allows the user to delete the record.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vaccineName: TextView = view.findViewById(R.id.vacccineName)
        val doseText: TextView = view.findViewById(R.id.doseText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val nextDose: TextView = view.findViewById(R.id.nextDose)
        val deleteRecord: Button = view.findViewById(R.id.deleteRecordButton)
    }
    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent an item.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new [ViewHolder] that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vacccination_history_item, parent, false)
        return ViewHolder(view)
    }
    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder The [ViewHolder] that should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("ResourceAsColor", "SetTextI18n")
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
                        holder.deleteRecord.text = "Deleted"
                    }
                } else {
                    // Handle the error
                }
            }
        }
    }

    /**
     * Calculates the date of the next dose based on the last vaccination date, the time between doses, the dose number, and the total number of doses.
     *
     * @param lastVaccinationDate The date of the last vaccination.
     * @param timeBetweenDoses The time between doses for the vaccine.
     * @param dose The dose number of the record.
     * @param numberOfDoses The total number of doses for the vaccine.
     * @return The date of the next dose.
     */
    private fun calculateNextDose(lastVaccinationDate: String, timeBetweenDoses: Int, dose: Int, numberOfDoses: Int): String {
        if (dose >= numberOfDoses) {
            return "No more doses needed"
        }

        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = format.parse(lastVaccinationDate)
        val calendar = Calendar.getInstance()
        calendar.time = date!!
        calendar.add(Calendar.DAY_OF_MONTH, timeBetweenDoses)

        val currentDate = Calendar.getInstance()
        val daysUntilNextDose = ((calendar.time.time - currentDate.time.time) / (1000 * 60 * 60 * 24)).toInt()

        return if (daysUntilNextDose > 0) {
            "Next Dose: in $daysUntilNextDose days"
        } else {
            "Next Dose: As soon as possible"
        }
    }
    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount() = records.size
}