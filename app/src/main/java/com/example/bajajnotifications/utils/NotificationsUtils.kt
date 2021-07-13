package com.example.bajajnotifications.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.example.bajajnotifications.MainActivity
import com.example.bajajnotifications.R
import com.example.bajajnotifications.TTS
import com.example.bajajnotifications.dataModels.ReceivedNotification
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

private const val TAG = "NotificationsUtils"

fun NotificationManager.sendNotification(
    notificationData: ReceivedNotification,
    applicationContext: Context
) {

    val notificationId = (Date().time / 1000L % Int.MAX_VALUE).toInt()

    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        notificationId,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val playIntent = Intent(applicationContext, TTS::class.java)
    playIntent.putExtra("text", notificationData.body)
    Log.d(TAG, "sendNotification: ${notificationData.body}")
    val playPendingIntent: PendingIntent = PendingIntent.getService(
        applicationContext,
        notificationId,
        playIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationImage = returnImage(notificationData.imageUrl, applicationContext)

    val bigPicStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(notificationImage)
        .bigLargeIcon(null)

    //Build notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.push_notification_channel_id)
    )

        .setSmallIcon(R.drawable.bajaj_logo)
        .setContentTitle(notificationData.title)
        .setContentText(notificationData.body)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSilent(true)
        .setContentIntent(contentPendingIntent)
        .setStyle(bigPicStyle)
        .setLargeIcon(notificationImage)

        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.play_sound),
            playPendingIntent
        )

    notify(notificationId, builder.build())

}

fun returnImage(imageUrl: String, applicationContext: Context): Bitmap {
    return try {
        val url = URL(imageUrl)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        BitmapFactory.decodeResource(
            applicationContext.resources,
            R.drawable.download
        )
    }
}
