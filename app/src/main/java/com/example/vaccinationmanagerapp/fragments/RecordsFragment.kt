package com.example.vaccinationmanagerapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.AddRecordActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.entity.RecordItem
import com.example.vaccinationmanagerapp.adapters.VaccinationHistoryListAdapter
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A [Fragment] subclass that represents the user's vaccination records.
 * It fetches the user's vaccination records from the database and displays them in a RecyclerView.
 * It also provides an option to add a new vaccination record.
 */
class RecordsFragment : Fragment() {

    /**
     * Inflates the layout for this fragment and returns the inflated View.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_records, container, false)
    }

    /**
     * Sets up the UI elements and their OnClickListeners after the view is created.
     * It sets up the RecyclerView for the vaccination records and the add record button.
     * It fetches the user's vaccination records from the database and displays them in the RecyclerView.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addRecordButton: Button = view.findViewById(R.id.addVaccinationButton)
        val emptyBoxImageView: ImageView = view.findViewById(R.id.emptyBox1)

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
                    status = appointment.status ?: status.Completed,
                    number_of_doses = appointment.number_of_doses ?: 0,
                    time_between_doses = appointment.time_between_doses ?: 0
                )
            }
            withContext(Dispatchers.Main) {
                if (recordItems.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyBoxImageView.visibility = View.VISIBLE
                } else {
                    recyclerView.adapter = VaccinationHistoryListAdapter(recordItems)
                    recyclerView.visibility = View.VISIBLE
                    emptyBoxImageView.visibility = View.GONE
                }
            }
        }
        val updateButton: ImageButton = view.findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            updateAppointmentsList(emptyBoxImageView)
        }

    }
    /**
     * Fetches the user's vaccination records from the database and updates the RecyclerView.
     * If there are no records, it displays an empty box image.
     * @param emptyBoxImageView The ImageView for the empty box image.
     */
    private fun updateAppointmentsList(emptyBoxImageView: ImageView? = null) {
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
                    status = appointment.status ?: status.Completed,
                    number_of_doses = appointment.number_of_doses ?: 0,
                    time_between_doses = appointment.time_between_doses ?: 0
                )
            }

            withContext(Dispatchers.Main) {
                if (recordItems.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyBoxImageView!!.visibility = View.VISIBLE
                } else {
                    recyclerView.adapter = VaccinationHistoryListAdapter(recordItems)
                    recyclerView.visibility = View.VISIBLE
                    emptyBoxImageView!!.visibility = View.GONE
                }
            }
        }
    }
}