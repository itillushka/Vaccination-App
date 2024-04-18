package com.example.vaccinationmanagerapp.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.SetAppointmentActivity
import com.example.vaccinationmanagerapp.adapters.AppointmentItem
import com.example.vaccinationmanagerapp.adapters.AppointmentsListAdapter
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AppointmentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppointmentFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setAppointmentButton: Button = view.findViewById(R.id.setAppointmentButton)
        setAppointmentButton.setOnClickListener {
            val intent = Intent(activity, SetAppointmentActivity::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.recycleViewAppointments)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        // Fetch the appointments from the database
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }
        // Start a new coroutine without blocking the current thread
        lifecycleScope.launch(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val appointments = dbQueries.getAppointmentsByUser(firebaseUserId)
            connection.close()

            // Convert the appointments into AppointmentItem objects
            val appointmentItems = appointments.map { appointment ->
                AppointmentItem(
                    appointment_id = appointment.appointment_id!!,
                    vaccine_name = appointment.vaccine_name.toString(),
                    date = appointment.date.toString(),
                    dose = appointment.dose ?: 0,
                    status = appointment.status ?: status.Scheduled
                )
            }

            // Update the RecyclerView adapter on the main thread
            withContext(Dispatchers.Main) {
                recyclerView.adapter = AppointmentsListAdapter(appointmentItems)
            }
        }
        val updateButton: ImageButton = view.findViewById(R.id.updateButton)
        updateButton.setOnClickListener {
            updateAppointmentsList()
        }


    }
    private fun updateAppointmentsList() {
        val recyclerView: RecyclerView = view?.findViewById(R.id.recycleViewAppointments) ?: return
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val appointments = dbQueries.getAppointmentsByUser(firebaseUserId)
            connection.close()

            val appointmentItems = appointments.map { appointment ->
                AppointmentItem(
                    appointment_id = appointment.appointment_id!!,
                    vaccine_name = appointment.vaccine_name.toString(),
                    date = appointment.date.toString(),
                    dose = appointment.dose ?: 0,
                    status = appointment.status ?: status.Scheduled
                )
            }

            withContext(Dispatchers.Main) {
                recyclerView.adapter = AppointmentsListAdapter(appointmentItems)
            }
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
class AppointmentDetailsDialogFragment(private val appointment_id: Int) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater: LayoutInflater = requireActivity().layoutInflater;
            val view = inflater.inflate(R.layout.appointment_details_popup, null)

            val closeButton: Button = view.findViewById(R.id.closeButtonAppmDetails)
            closeButton.setOnClickListener {
                dismiss()
            }
            Log.d("appm_id", "appm_id: $appointment_id")

            lifecycleScope.launch(Dispatchers.IO) {
                val connection = DBconnection.getConnection()
                val dbQueries = AppointmentDBQueries(connection)
                val details = dbQueries.fetchAppointmentDetails(appointment_id)
                connection.close()

                withContext(Dispatchers.Main) {
                    val dateView: TextView = view.findViewById(R.id.dateAppmDetails)
                    val timeView: TextView = view.findViewById(R.id.timeAppmDetails)
                    val nameView: TextView = view.findViewById(R.id.fullName)
                    val ageView: TextView = view.findViewById(R.id.age)
                    val doseView: TextView = view.findViewById(R.id.dose)
                    val genderView: TextView = view.findViewById(R.id.gender)
                    val vaccineNameView: TextView = view.findViewById(R.id.vaccineName)
                    val statusImage: ImageView = view.findViewById(R.id.appmDetailsStatus)

                    dateView.text = details[0]
                    timeView.text = details[1]
                    nameView.text = details[2]
                    ageView.text = details[3]
                    doseView.text = details[4]
                    genderView.text = details[5]
                    vaccineNameView.text = details[6]
                    val status = details[7]
                    if (status == "Completed") {
                        statusImage.setImageResource(R.drawable.completed_appm_icon)
                    } else if (status == "Canceled") {
                        statusImage.setImageResource(R.drawable.canceled_appm_icon)
                    } else {
                        statusImage.setImageResource(R.drawable.upcoming_appm_icon)
                    }

                }
            }

            builder.setView(view)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}