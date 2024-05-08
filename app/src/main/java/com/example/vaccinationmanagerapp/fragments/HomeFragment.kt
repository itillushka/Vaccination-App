package com.example.vaccinationmanagerapp.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.appointment.AppointmentDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
import com.example.vaccinationmanagerapp.mySQLDatabase.users.UsersDBQueries
import java.util.Calendar

/**
 * A [Fragment] subclass that represents the home screen of the application.
 * It displays the user's upcoming appointment, last vaccination record, and last notification.
 * It also provides navigation to the corresponding details screens.
 */
class HomeFragment : Fragment() {
    /**
     * Sets up the UI elements and their OnClickListeners after the view is created.
     * It sets up the upcoming appointment, last record, and last notification sections.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // functionality which we cannot implement properly
        val upcomingAppointmentButton: ImageButton = view.findViewById(R.id.upcAppmArrowButton)
        val lastRecordButton: ImageButton = view.findViewById(R.id.recordsArrowButton)
        val notificationButton: ImageButton = view.findViewById(R.id.notificationArrowButton)
        /*
        upcomingAppointmentButton.setOnClickListener {
            findNavController().navigate(R.id.appointmentFragment)
        }

        lastRecordButton.setOnClickListener {
            findNavController().navigate(R.id.recordsFragment)
        }

        notificationButton.setOnClickListener {
            findNavController().navigate(R.id.notificationFragment)
        }
         */
    }

    private lateinit var upcomingAppointment: View
    private lateinit var lastRecord: View
    private lateinit var lastNotification: View

    /**
     * Inflates the layout for this fragment and returns the inflated View.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        upcomingAppointment = view.findViewById(R.id.upcomingAppointment)
        lastRecord = view.findViewById(R.id.lastRecord)
        lastNotification = view.findViewById(R.id.lastNotification)
        val welcomeText: TextView = view.findViewById(R.id.welcomeText)


        CoroutineScope(Dispatchers.IO).launch {
            var connection = DBconnection.getConnection()
            val dbQueries = AppointmentDBQueries(connection)
            val firebaseAuth = FirebaseAuth.getInstance()
            val firebaseUser = firebaseAuth.currentUser
            var firebaseUserId : String = ""
            if (firebaseUser != null) {
                firebaseUserId = firebaseUser.uid
            }

            val appointment = dbQueries.getUpcomingAppointment(firebaseUserId)
            val record = dbQueries.getLastVaccinationRecord(firebaseUserId)
            val usersDBQueries = UsersDBQueries(connection)
            val user = usersDBQueries.fetchUserData(firebaseUserId)
            withContext(Dispatchers.Main) {
                welcomeText.text = "Welcome, ${user[0]}!"
            }
            connection.close()

            // Get the notifications from the database
            connection = DBconnection.getConnection()
            val notificationDBQueries = NotificationsDBQueries(connection)
            val notifications = notificationDBQueries.getNotifications(firebaseUserId)
            connection.close()

            if(notifications.isEmpty()) {
                withContext(Dispatchers.Main) {
                    val notificationTitle: TextView = lastNotification.findViewById(R.id.textViewNotificationTitle)
                    val notificationBody: TextView = lastNotification.findViewById(R.id.textViewNotificationBody)
                    val notificationDate: TextView = lastNotification.findViewById(R.id.textViewNotificationsDate)

                    notificationTitle.text = "No notifications"
                    notificationBody.text = "You have no new notifications"
                    notificationDate.text = ""
                }
            } else {
                // Sort the notifications by notification_id in descending order and take the first
                val sortedNotification = notifications.sortedByDescending { it.notification_id }.take(1)

                withContext(Dispatchers.Main) {
                    val notificationTitle: TextView = lastNotification.findViewById(R.id.textViewNotificationTitle)
                    val notificationBody: TextView = lastNotification.findViewById(R.id.textViewNotificationBody)
                    val notificationDate: TextView = lastNotification.findViewById(R.id.textViewNotificationsDate)

                    notificationTitle.text = sortedNotification[0].title
                    notificationBody.text = sortedNotification[0].description
                    notificationDate.text = sortedNotification[0].date_sent?.toString()?.substring(0,10)
                }
            }


            if (appointment.appointment_id != 0) {
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
            } else {
                withContext(Dispatchers.Main) {
                    val appmName: TextView = upcomingAppointment.findViewById(R.id.appointmentName)
                    val dateAppText: TextView = upcomingAppointment.findViewById(R.id.dateAppText)
                    val doseAppText: TextView = upcomingAppointment.findViewById(R.id.doseAppText)
                    val appointmentStatus: ImageView = upcomingAppointment.findViewById(R.id.appointmentStatus)
                    val cancelBookingButton: Button = upcomingAppointment.findViewById(R.id.cancelBookingButton)
                    val seeDetailsButton: Button = upcomingAppointment.findViewById(R.id.seeDetailsButton)

                    appmName.text = "No information available"
                    dateAppText.text = "Date: No Data"
                    doseAppText.text = "Dose: No Data"

                    cancelBookingButton.setTextColor(R.color.gray)
                    cancelBookingButton.setBackgroundResource(R.drawable.unavailable_button)
                    cancelBookingButton.isClickable = false

                    seeDetailsButton.setTextColor(R.color.gray)
                    seeDetailsButton.setBackgroundResource(R.drawable.unavailable_button)
                    seeDetailsButton.isClickable = false

                    appointmentStatus.setImageResource(R.drawable.unavailable_status_icon)
                }
            }

            if(record.appointment_id != 0) {
                withContext(Dispatchers.Main) {
                    val vaccineName: TextView = lastRecord.findViewById(R.id.vacccineName)
                    val doseText: TextView = lastRecord.findViewById(R.id.doseText)
                    val dateText: TextView = lastRecord.findViewById(R.id.dateText)
                    val nextDose: TextView = lastRecord.findViewById(R.id.nextDose)
                    val deleteRecord: Button = lastRecord.findViewById(R.id.deleteRecordButton)

                    vaccineName.text = record.vaccine_name
                    doseText.text = "Dose: ${record.dose}"
                    dateText.text = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(record.date)
                    nextDose.text = calculateNextDose(record.date?.toString() ?: "",
                        record.time_between_doses ?: 0,
                        record.dose ?: 0,
                        record.number_of_doses ?: 0)

                    deleteRecord.setOnClickListener {
                        val appointmentId = record.appointment_id

                        CoroutineScope(Dispatchers.IO).launch {
                            val connection = DBconnection.getConnection()
                            val dbQueries = AppointmentDBQueries(connection)

                            val result = dbQueries.deleteAppointment(appointmentId!!)

                            connection.close()

                            if (result) {
                                withContext(Dispatchers.Main) {
                                    deleteRecord.setTextColor(R.color.gray)
                                    deleteRecord.setBackgroundResource(R.drawable.unavailable_button)
                                    deleteRecord.setText("Deleted")
                                }
                            } else {
                                // Handle the error
                            }
                        }
                    }
                }
            }
            else{
                withContext(Dispatchers.Main) {
                    val vaccineName: TextView = lastRecord.findViewById(R.id.vacccineName)
                    val doseText: TextView = lastRecord.findViewById(R.id.doseText)
                    val dateText: TextView = lastRecord.findViewById(R.id.dateText)
                    val nextDose: TextView = lastRecord.findViewById(R.id.nextDose)
                    val deleteRecord: Button = lastRecord.findViewById(R.id.deleteRecordButton)

                    vaccineName.text = "No information available"
                    doseText.text = "Dose: No Data"
                    dateText.text = "Date: No Data"
                    nextDose.text = "Next Dose: No Data"

                    deleteRecord.setTextColor(R.color.gray)
                    deleteRecord.setBackgroundResource(R.drawable.unavailable_button)
                    deleteRecord.isClickable = false
                }
            }
        }

        return view
    }

    /**
     * Calculates the date of the next dose based on the last vaccination date, the time between doses, the dose number, and the total number of doses.
     * @param lastVaccinationDate The date of the last vaccination.
     * @param timeBetweenDoses The time between doses in days.
     * @param dose The dose number.
     * @param numberOfDoses The total number of doses.
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
}