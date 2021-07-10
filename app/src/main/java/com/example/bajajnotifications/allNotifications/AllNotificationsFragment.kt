package com.example.bajajnotifications.allNotifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.bajajnotifications.R
import com.example.bajajnotifications.databinding.FragmentAllNotificationsBinding
import com.google.firebase.messaging.FirebaseMessaging

class AllNotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllNotificationsBinding.inflate(inflater)

        val viewModel = ViewModelProvider(this).get(AllNotificationsViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        createChannel(
            getString(R.string.push_notification_channel_id),
            getString(R.string.push_notification_channel_name)
        )

        subscribeTopic()

        return binding.root
    }

    private fun createChannel(channelId: String, channelName: String) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.push_notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.test))
            .addOnCompleteListener { task ->
                var message = "Done Subscribe"
                if (!task.isSuccessful) {
                    message = "Subscribed"
                }
                Log.i("status", message)
            }
    }

}