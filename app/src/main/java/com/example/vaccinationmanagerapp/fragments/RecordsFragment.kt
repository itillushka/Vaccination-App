package com.example.vaccinationmanagerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.AddRecordActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.SetAppointmentActivity
import com.example.vaccinationmanagerapp.adapters.AppointmentItem
import com.example.vaccinationmanagerapp.adapters.AppointmentsListAdapter
import com.example.vaccinationmanagerapp.adapters.RecordItem
import com.example.vaccinationmanagerapp.adapters.VaccinationHistoryListAdapter
import com.example.vaccinationmanagerapp.models.VaccinationRecord
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    private lateinit var vaccinationRecordsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addRecordButton: Button = view.findViewById(R.id.addVaccinationButton)
        addRecordButton.setOnClickListener {
            val intent = Intent(activity, AddRecordActivity::class.java)
            startActivity(intent)
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.vaccinationRecordsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val appointments = dbQueries.getRecordsByUser(firebaseUserId)
            connection.close()

            val recordItems = appointments.map { appointment ->
                RecordItem(
                    appointment_id = appointment.appointment_id!!,
                    vaccine_name = appointment.vaccine_name.toString(),
                    date = appointment.date.toString(),
                    dose = appointment.dose ?: 0,
                    status = appointment.status ?: status.Completed
                )
            }

            withContext(Dispatchers.Main) {
                recyclerView.adapter = VaccinationHistoryListAdapter(recordItems)
            }
        }
        val updateButton: ImageButton = view.findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            updateAppointmentsList()
        }

    }
    private fun updateAppointmentsList() {
        val recyclerView: RecyclerView = view?.findViewById(R.id.vaccinationRecordsRecyclerView) ?: return
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val appointments = dbQueries.getRecordsByUser(firebaseUserId)
            connection.close()

            val recordItems = appointments.map { appointment ->
                RecordItem(
                    appointment_id = appointment.appointment_id!!,
                    vaccine_name = appointment.vaccine_name.toString(),
                    date = appointment.date.toString(),
                    dose = appointment.dose ?: 0,
                    status = appointment.status ?: status.Completed
                )
            }

            withContext(Dispatchers.Main) {
                recyclerView.adapter = VaccinationHistoryListAdapter(recordItems)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}