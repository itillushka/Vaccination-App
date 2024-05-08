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
import com.example.vaccinationmanagerapp.adapters.NotificationItem
import com.example.vaccinationmanagerapp.adapters.RecentNotificationsAdapter
import com.example.vaccinationmanagerapp.mySQLDatabase.DBconnection
import com.example.vaccinationmanagerapp.mySQLDatabase.notifications.NotificationsDBQueries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * A simple [Fragment] subclass.
 * This fragment is responsible for displaying the user's notifications.
 * It fetches the notifications from the database and displays them in a RecyclerView.
 */
class NotificationFragment : Fragment() {
    /**
     * This function is called when the fragment is created.
     * It sets up the RecyclerViews for the recent notifications and all notifications.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }
    /**
     * This function is called after the view is created.
     * It fetches the notifications from the database and sets up the RecyclerViews.
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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
}