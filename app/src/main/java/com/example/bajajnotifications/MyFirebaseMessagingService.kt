package com.example.bajajnotifications

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.bajajnotifications.utils.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        remoteMessage.data.let {
            Log.d(TAG, "onMessageReceived Payload: $it")
            sendNotification(it["body"] ?: "Empty Message Body")
        }

        remoteMessage.notification.let {
            Log.d(TAG, "onMessageReceived Notification: ${it?.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {

    }

    private fun sendNotification(messageBody: String) {
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.sendNotification(messageBody, applicationContext)
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}