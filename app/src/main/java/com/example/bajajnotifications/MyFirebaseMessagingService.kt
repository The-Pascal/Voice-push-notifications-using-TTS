package com.example.bajajnotifications

import android.app.NotificationManager
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.bajajnotifications.utils.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private lateinit var tts: TextToSpeech
    private lateinit var text: String

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "onMessageReceived: $remoteMessage")

        remoteMessage.data.let {
            Log.d(TAG, "onMessageReceived Payload: $it")
            text = it["body"] ?: "Empty Message body"
            sendNotification(text)

            val ttsIntent = Intent(baseContext, TTS::class.java)
            ttsIntent.putExtra("text", text)
            startService(ttsIntent)
        }

        remoteMessage.notification.let {
            Log.d(TAG, "onMessageReceived Notification: ${it?.body}")
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "onNewToken: $token")
        sendRegistrationToServer(token)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Service is created successfully")
    }

    private fun sendRegistrationToServer(token: String) {

    }

    private fun sendNotification(messageBody: String) {
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.sendNotification(messageBody, applicationContext)
    }

    private fun speak() {
        if (tts != null) {
            Log.d(TAG, "speak: Speaking start.....")
            if (text != null)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service is destroyed successfully")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}