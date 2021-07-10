package com.example.bajajnotifications.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ReadNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if(intent?.extras != null) {
            val text = intent.getStringExtra("text").toString()
            Log.d(TAG, "onReceive: $text")
        }
    }

    companion object {
        private const val TAG = "ReadNotificationReceive"
    }
}