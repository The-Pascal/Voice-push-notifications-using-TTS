package com.example.bajajnotifications

import android.app.NotificationManager
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.bajajnotifications.receiver.ReadNotificationReceiver
import com.example.bajajnotifications.utils.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService: FirebaseMessagingService(), TextToSpeech.OnInitListener {

    private lateinit var tts: TextToSpeech
    private lateinit var text: String

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "onMessageReceived: $remoteMessage")

        remoteMessage.data.let {
            Log.d(TAG, "onMessageReceived Payload: $it")
            tts = TextToSpeech(applicationContext, this)
            text = it["body"] ?: "Empty Message body"
            sendNotification(text)
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
        val notificationManager = ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.sendNotification(messageBody, applicationContext)
    }

    override fun onInit(status: Int) {
        Log.d(TAG, "onInit: started")
        if(status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("hi","IN"))
            if(result != TextToSpeech.LANG_MISSING_DATA
                && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    addAudioAttributes()
                    speak()
            }
        }
    }

    private fun addAudioAttributes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()
            tts.setAudioAttributes(audioAttributes)
        }
    }

    private fun speak() {
        if(tts != null) {
            Log.d(TAG, "speak: Speaking start.....")
            if(text != null)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service is destroyed successfully")
        if(tts!=null) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}