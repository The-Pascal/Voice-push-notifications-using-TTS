package com.example.bajajnotifications

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import android.os.IBinder
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Toast
import java.util.*


class TTS : Service(), OnInitListener {
    private var mTts: TextToSpeech? = null
    private lateinit var spokenText: String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.extras != null) {
            spokenText = intent.getStringExtra("text").toString()
        }
        else{
            spokenText = ""
        }
        Log.d(TAG, "onStartCommand: $spokenText")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        mTts = TextToSpeech(this, this)
        Log.d(TAG, "onCreate: CREATING AGAIN !!")
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "onInit: TextToSpeech Success")
            val result = mTts!!.setLanguage(Locale("hi", "IN"))
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.d(TAG, "onInit: speaking........")
                addAudioAttributes()
                speak()
            }
            else {
                Log.d(TAG, "onInit: Language not supported")
                Toast.makeText(applicationContext, "Language not supported", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Log.d(TAG, "onInit: TTS not initializing")
        }
    }

    private fun addAudioAttributes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mTts?.setAudioAttributes(audioAttributes)
        }
    }

    private fun speak() {
        val speechListener = object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d(TAG, "onStart: Started syntheses.....")
            }

            override fun onDone(utteranceId: String?) {
                Log.d(TAG, "onDone: Completed synthesis ")
                stopSelf()
            }

            override fun onError(utteranceId: String?) {
                Log.d(TAG, "onError: Error synthesis")
            }
        }
        val paramsMap: HashMap<String, String> = HashMap()
        paramsMap[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = "ThreeArgument"

        mTts?.speak(spokenText, TextToSpeech.QUEUE_FLUSH, paramsMap)
        mTts?.setOnUtteranceProgressListener(speechListener)
    }

    override fun onDestroy() {
        if (mTts != null) {
            Log.d(TAG, "onDestroy: destroyed tts")
            mTts!!.stop()
            mTts!!.shutdown()
        }
        super.onDestroy()
    }

    override fun onBind(arg0: Intent?): IBinder? {
        return null
    }
    
    companion object {
        private const val TAG = "TTS"
    }
}
