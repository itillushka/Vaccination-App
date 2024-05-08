package com.example.vaccinationmanagerapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationmanagerapp.R
import com.example.vaccinationmanagerapp.adapters.AppointmentItem
import com.example.vaccinationmanagerapp.adapters.NotificationItem
import com.example.vaccinationmanagerapp.adapters.RecentNotificationsAdapter
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
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
 * Use the [NotificationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the current user's firebase user id
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseUser = firebaseAuth.currentUser
        var firebaseUserId : String = ""
        if (firebaseUser != null) {
            firebaseUserId = firebaseUser.uid
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // Get the notifications from the database
            val connection = DBconnection.getConnection()
            val notificationDBQueries = NotificationsDBQueries(connection)
            val notifications = notificationDBQueries.getNotifications(firebaseUserId)

            // Sort the notifications by notification_id in descending order and take the first 3
            val sortedNotifications = notifications.sortedByDescending { it.notification_id }.take(3)

            // Convert the notifications to NotificationItems
            val notificationItems = sortedNotifications.map { notification ->
                NotificationItem(
                    notification.title ?: "",
                    notification.description ?: "",
                    notification.date_sent?.toString()?.substring(0,10) ?: ""
                )
            }

            // For all notifications
            val allNotificationItems = notifications.map { notification ->
                NotificationItem(
                    notification.title ?: "",
                    notification.description ?: "",
                    notification.date_sent?.toString()?.substring(0,10) ?: ""
                )
            }

            withContext(Dispatchers.Main) {
                // Set up the RecyclerView
                val recyclerView: RecyclerView = view.findViewById(R.id.recentNotificationsRecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(activity)
                recyclerView.adapter = RecentNotificationsAdapter(notificationItems)

                val recyclerView1: RecyclerView = view.findViewById(R.id.allNotificationsRecycleView)
                recyclerView1.layoutManager = LinearLayoutManager(activity)
                recyclerView1.adapter = RecentNotificationsAdapter(allNotificationItems)
            }

            connection.close()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}