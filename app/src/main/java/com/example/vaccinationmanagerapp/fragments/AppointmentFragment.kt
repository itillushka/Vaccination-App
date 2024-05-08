package com.example.vaccinationmanagerapp.fragments

import android.annotation.SuppressLint
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
import com.example.vaccinationmanagerapp.adapters.AppointmentsListAdapter
import com.example.vaccinationmanagerapp.entity.AppointmentItem
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A [Fragment] subclass that represents the user's appointments.
 * It fetches the user's appointments from the database and displays them in a RecyclerView.
 * It also provides an option to set a new appointment.
 */
class AppointmentFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    /**
     * Sets up the UI elements and their OnClickListeners after the view is created.
     * It sets up the RecyclerView for the appointments and the set appointment button.
     * It fetches the user's appointments from the database and displays them in the RecyclerView.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val setAppointmentButton: Button = view.findViewById(R.id.setAppointmentButton)
        val emptyBoxImageView: ImageView = view.findViewById(R.id.emptyBox)

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

            withContext(Dispatchers.Main) {
                if (appointmentItems.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyBoxImageView.visibility = View.VISIBLE
                } else {
                    recyclerView.adapter = AppointmentsListAdapter(appointmentItems)
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
     * Updates the appointments list in the fragment.
     * If there are no appointments, it displays an empty box image.
     * @param emptyBoxImageView The ImageView for the empty box image.
     */
    private fun updateAppointmentsList(emptyBoxImageView: ImageView? = null) {
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
                if (appointmentItems.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyBoxImageView!!.visibility = View.VISIBLE
                } else {
                    recyclerView.adapter = AppointmentsListAdapter(appointmentItems)
                    recyclerView.visibility = View.VISIBLE
                    emptyBoxImageView!!.visibility = View.GONE
                }
            }
        }
    }

}

/**
 * A dialog fragment that displays the details of an appointment.
 * @property appointment_id The unique identifier of the appointment.
 */
class AppointmentDetailsDialogFragment(private val appointment_id: Int) : DialogFragment() {

    /**
     * Creates and returns the dialog to be shown.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     */
    @SuppressLint("SetTextI18n")
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
                    nameView.text = "Full Name: " + details[2]
                    ageView.text = "Age: " + details[3]
                    doseView.text = "Dose: " + details[4]
                    genderView.text = "Gender: " + details[5]
                    vaccineNameView.text = "Vaccine Name: " + details[6]
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