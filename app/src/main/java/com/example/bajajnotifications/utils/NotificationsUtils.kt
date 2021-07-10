package com.example.bajajnotifications.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.bajajnotifications.R
import com.example.bajajnotifications.TTS
import java.util.*

private const val REQUEST_CODE = 0
private const val FLAGS = 0
private const val TAG = "NotificationsUtils"

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {

    val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()

    val playIntent = Intent(applicationContext, TTS::class.java)
    playIntent.putExtra("text", messageBody)
    Log.d(TAG, "sendNotification: $messageBody")
    val playPendingIntent: PendingIntent = PendingIntent.getService(
        applicationContext,
        notificationId,
        playIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

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

        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.play),
            playPendingIntent
        )

    notify(notificationId, builder.build())

}
