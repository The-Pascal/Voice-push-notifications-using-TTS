package com.example.bajajnotifications

import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.bajajnotifications.dataModels.ReceivedNotification
import com.example.bajajnotifications.utils.sendNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*
import com.example.bajajnotifications.db.AppDatabase;
import com.example.bajajnotifications.db.Notifications
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyFirebaseMessagingService : FirebaseMessagingService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech?= null
    private lateinit var text: String

    private val db by lazy{
        AppDatabase.getDatabase(this)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "onMessageReceived: $remoteMessage")

        remoteMessage.data.let {
            Log.d(TAG, "onMessageReceived Payload: $it")
            text = it["body"] ?: ""
            tts = TextToSpeech(applicationContext, this)

            val title : String = it["title"] ?: "Empty Title"
            val description : String = it["body"] ?: "Empty description"
            val imageUri : String = it["image"] ?: "Empty Uri"

            val notificationData = ReceivedNotification()
            notificationData.title = title
            notificationData.body = description
            notificationData.imageUrl = imageUri
            sendNotification(notificationData)
            saveNotification(title,description,imageUri)

        }

        remoteMessage.notification.let {
            Log.d(TAG, "onMessageReceived Notification: ${it?.body}")
        }

    }

    private fun saveNotification(title : String, description : String, imageUri : String) {

        GlobalScope.launch(Dispatchers.Main){
            val id = withContext(Dispatchers.IO){
                return@withContext db.notificationDao().insertNotification(
                    Notifications(
                        title,
                        description,
                        imageUri
                    )
                )
            }
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

    private fun sendNotification(notificationData: ReceivedNotification) {
        val notificationManager =
            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java)
        notificationManager?.sendNotification(notificationData, applicationContext)
    }

    override fun onInit(status: Int) {
        Log.d(TAG, "onInit: started")
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("hi", "IN"))
            if (result != TextToSpeech.LANG_MISSING_DATA
                && result != TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                addAudioAttributes()
            }
        } else {
            Log.d(TAG, "TTS initialization failed ")
            Toast.makeText(
                applicationContext,
                "Your device don't support text to speech.\n Visit app to download!!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun addAudioAttributes() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { focus ->
                        when (focus) {
                            AudioManager.AUDIOFOCUS_GAIN -> {
                            }
                            else -> stopSelf()
                        }
                    }.build()

            when (audioManager.requestAudioFocus(focusRequest)) {
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> speak(audioManager, focusRequest)
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> stopSelf()
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> stopSelf()
            }

        } else {
            val result = audioManager.requestAudioFocus( { focusChange: Int ->
                when(focusChange) {
                    AudioManager.AUDIOFOCUS_GAIN -> {
                    }
                    else -> stopSelf()
                }
                },
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                speak(audioManager, null)
            }
        }
    }

    private fun speak(audioManager: AudioManager, focusRequest: AudioFocusRequest?) {
        if (tts != null) {
            Log.d(TAG, "speak: Speaking start.....")
            val speechListener = object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    Log.d(TAG, "onStart: Started syntheses.....")
                }

                override fun onDone(utteranceId: String?) {
                    Log.d(TAG, "onDone: Completed synthesis ")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
                        audioManager.abandonAudioFocusRequest(focusRequest)
                    }
                    stopSelf()
                }

                override fun onError(utteranceId: String?) {
                    Log.d(TAG, "onError: Error synthesis")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && focusRequest != null) {
                        audioManager.abandonAudioFocusRequest(focusRequest)
                    }
                    stopSelf()
                }
            }
            val paramsMap: HashMap<String, String> = HashMap()
            paramsMap[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "tts_firebase_service"

            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, paramsMap)
            tts?.setOnUtteranceProgressListener(speechListener)
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: Service is destroyed successfully")
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}