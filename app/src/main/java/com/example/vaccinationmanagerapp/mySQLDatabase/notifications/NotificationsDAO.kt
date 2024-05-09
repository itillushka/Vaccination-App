package com.example.vaccinationmanagerapp.mySQLDatabase.notifications

/**
 * Interface for Notification Data Access Object.
 * This interface defines the methods for interacting with the Notification data in the database.
 */
interface NotificationsDAO {

    /**
     * Inserts a new notification into the database.
     * @param notification The notification to be inserted.
     * @return Boolean indicating success or failure.
     */
    fun insertNotifications(notification: Notifications): Boolean

    /**
     * Retrieves all notifications for a specific user from the database.
     * @param firebase_user_id The Firebase user ID of the user.
     * @return List of Notifications.
     */
    fun getNotifications(firebase_user_id: String): List<Notifications>
}