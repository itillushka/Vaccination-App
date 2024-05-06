package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

import java.sql.Connection
import java.sql.Timestamp
import java.sql.Types

class NotificationsDBQueries(private val connection: Connection) : NotificationsDAO {

    override fun insertNotifications(notification: Notifications): Boolean {
        val call = "{CALL insertNotifications(?,?,?,?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, notification.firebase_user_id)
        statement.setTimestamp(2, notification.date_sent)
        statement.setString(3, notification.title)
        statement.setString(4, notification.description)
        return statement.execute()
    }

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