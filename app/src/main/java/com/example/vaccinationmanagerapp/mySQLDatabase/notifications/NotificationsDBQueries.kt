package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

import java.sql.Connection
import java.sql.Timestamp
import java.sql.Types

/**
 * Class for Notification Database Queries.
 * This class implements the NotificationsDAO interface and defines the methods for interacting with the Notification data in the database.
 * @property connection The connection to the database.
 */
class NotificationsDBQueries(private val connection: Connection) : NotificationsDAO {

    /**
     * Inserts a new notification into the database.
     * @param notification The notification to be inserted.
     * @return Boolean indicating success or failure.
     */
    override fun insertNotifications(notification: Notifications): Boolean {
        val call = "{CALL insertNotifications(?,?,?,?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, notification.firebase_user_id)
        statement.setTimestamp(2, notification.date_sent)
        statement.setString(3, notification.title)
        statement.setString(4, notification.description)
        return statement.execute()
    }

    /**
     * Retrieves all notifications for a specific user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return List of Notifications.
     */
    override fun getNotifications(firebase_user_id: String): List<Notifications> {
        val call = "{CALL getAllNotificationsForUser(?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, firebase_user_id)
        val resultSet = statement.executeQuery()
        val notifications = mutableListOf<Notifications>()
        while (resultSet.next()) {
            val notification_id = resultSet.getInt("notification_id")
            val date_sent = resultSet.getTimestamp("date_sent")
            val title = resultSet.getString("title")
            val description = resultSet.getString("description")
            notifications.add(Notifications(notification_id, firebase_user_id, date_sent, title, description))
        }
        return notifications
    }
}