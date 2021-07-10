package com.example.bajajnotifications.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.bajajnotifications.TTS

class ReadNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive: Outside if")
            Log.d(TAG, "onReceive: Inside if")
            val text = intent?.getStringExtra("text")
            Log.d(TAG, "onReceive: $text")
            val ttsIntent = Intent(context, TTS::class.java)
            ttsIntent.putExtra("text", text)
            context.startService(ttsIntent)
    }

    companion object {
        private const val TAG = "ReadNotificationReceive"
    }
}