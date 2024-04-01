package com.example.vaccinationmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.models.VaccinationRecord

class VaccinationHistoryListAdapter(private val records: List<VaccinationRecord>) :
    RecyclerView.Adapter<VaccinationHistoryListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vaccineName: TextView = view.findViewById(R.id.vacccineName)
        val doseText: TextView = view.findViewById(R.id.doseText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val nextDose: TextView = view.findViewById(R.id.nextDose)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.vacccination_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.vaccineName.text = "VaccineName: ${record.vaccineName}"
        holder.doseText.text = "Dose: ${record.dose}"
        holder.dateText.text = record.date
        holder.nextDose.text = "Next Dose: ${record.nextDose}"
    }

    override fun getItemCount() = records.size
}