package com.example.vaccinationmanagerapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "com.example.vaccinationmanagerapp"

/**
 * This service handles Firebase Cloud Messaging (FCM) messages.
 * It can generate a notification when a message is received.
 * The notification displays the title and body of the message.
 * When the notification is clicked, it opens the MainActivity.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    /**
     * Returns a RemoteViews object that can be used to build a custom notification layout.
     * @param title The title of the notification.
     * @param message The message of the notification.
     * @return A RemoteViews object containing the notification layout.
     */
    fun getRemoteView(title: String, message: String) : RemoteViews {
        val remoteView = RemoteViews("com.example.vaccinationmanagerapp", R.layout.notification)

        remoteView.setTextViewText(R.id.notification_title, title)
        remoteView.setTextViewText(R.id.notification_description, message)

        return remoteView

    }

    /**
     * Generates a notification with the given title and message.
     * @param context The context in which the notification is generated.
     * @param title The title of the notification.
     * @param message The message of the notification.
     */
    fun generateNotification(context: Context, title: String, message: String){

    val intent = Intent(context, MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE)

    var builder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.notification_icon)
        .setAutoCancel(true)
        .setVibrate(longArrayOf(1000,1000,1000,1000))
        .setOnlyAlertOnce(true)
        .setContentIntent(pendingIntent)

    builder = builder.setContent(getRemoteView(title, message))

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(notificationChannel)
    }

    notificationManager.notify(0, builder.build())
}
}