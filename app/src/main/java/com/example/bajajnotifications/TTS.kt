package com.example.bajajnotifications

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*


class TTS : Service(), OnInitListener {
    private var tts: TextToSpeech? = null
    private lateinit var spokenText: String
    private var isInit: Boolean = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.extras != null) {
            spokenText = intent.getStringExtra("text").toString()
        }
        else {
            spokenText = ""
        }
        Log.d(TAG, "onStartCommand: $spokenText")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        tts = TextToSpeech(this, this)
        Log.d(TAG, "onCreate: CREATING AGAIN !!")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "onInit: TextToSpeech Success")
            val result = tts!!.setLanguage(Locale("hi", "IN"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d(TAG, "onInit: speaking........")
                addAudioAttributes()
                isInit = true
            }
        }
        else {
            Log.d(TAG, "onInit: TTS initialization failed")
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
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            tts?.setAudioAttributes(audioAttributes)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val focusRequest =
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
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
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK
            )

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                speak(audioManager, null)
            }
        }
    }

    private fun speak(audioManager: AudioManager, focusRequest: AudioFocusRequest?) {
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
        paramsMap[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "tts_service"

        tts?.speak(spokenText, TextToSpeech.QUEUE_FLUSH, paramsMap)
        tts?.setOnUtteranceProgressListener(speechListener)
    }

    override fun onDestroy() {
        if (tts != null) {
            Log.d(TAG, "onDestroy: destroyed tts")
            tts?.stop()
            tts?.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }
    
    companion object {
        private const val TAG = "TTS_Service"
    }
}
