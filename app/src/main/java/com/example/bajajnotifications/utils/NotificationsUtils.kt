package com.example.bajajnotifications.utils

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.bajajnotifications.R
import java.util.*

private const val REQUEST_CODE = 0
private const val FLAGS = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.push_notification_channel_id)
    )

        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(null)

    notify(notificationId, builder.build())

}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}