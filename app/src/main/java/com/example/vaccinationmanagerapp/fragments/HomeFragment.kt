package com.example.vaccinationmanagerapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.status
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
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
    private lateinit var upcomingAppointment: View

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        upcomingAppointment = view.findViewById(R.id.upcomingAppointment)

        CoroutineScope(Dispatchers.IO).launch {
            val connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val firebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = firebaseAuth.currentUser
            var firebaseUserId : String = ""
            if (firebaseUser != null) {
                firebaseUserId = firebaseUser.uid
            }

            val appointment = dbQueries.getUpcomingAppointment(firebaseUserId)
            connection.close()

            if (appointment != null) {
                withContext(Dispatchers.Main) {
                    // Fill the TextViews
                    val appmName: TextView = upcomingAppointment.findViewById(R.id.appointmentName)
                    val dateAppText: TextView = upcomingAppointment.findViewById(R.id.dateAppText)
                    val doseAppText: TextView = upcomingAppointment.findViewById(R.id.doseAppText)
                    val appointmentStatus: ImageView = upcomingAppointment.findViewById(R.id.appointmentStatus)

                    appmName.text = appointment.vaccine_name
                    dateAppText.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(appointment.date)
                    doseAppText.text = "Dose: ${appointment.dose}"

                    appointmentStatus.setImageResource(R.drawable.upcoming_appm_icon)

                    val cancelBookingButton: Button = upcomingAppointment.findViewById(R.id.cancelBookingButton)
                    cancelBookingButton.setOnClickListener {
                        CoroutineScope(Dispatchers.IO).launch {
                            val cancelResult = dbQueries.cancelAppointment(appointment.appointment_id!!)
                            if (cancelResult) {
                                withContext(Dispatchers.Main) {
                                    appointmentStatus.setImageResource(R.drawable.canceled_appm_icon)
                                    cancelBookingButton.setTextColor(R.color.gray)
                                    cancelBookingButton.setBackgroundResource(R.drawable.unavailable_button)
                                    cancelBookingButton.text = "Canceled"
                                }
                            }
                        }
                    }

                    val seeDetailsButton: Button = upcomingAppointment.findViewById(R.id.seeDetailsButton)
                    seeDetailsButton.setOnClickListener {
                        val dialogFragment = AppointmentDetailsDialogFragment(appointment.appointment_id!!)
                        val fragmentManager = (view.context as AppCompatActivity).supportFragmentManager
                        dialogFragment.show(fragmentManager, "AppointmentDetailsDialogFragment")
                    }
                }
            }
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}