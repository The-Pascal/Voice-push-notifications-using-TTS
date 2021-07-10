package com.example.bajajnotifications.allNotifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.bajajnotifications.R
import com.example.bajajnotifications.databinding.FragmentAllNotificationsBinding

class AllNotificationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAllNotificationsBinding.inflate(inflater)

        binding.lifecycleOwner = this

        val viewModel = ViewModelProvider(this).get(AllNotificationsViewModel::class.java)

        binding.viewModel = viewModel

        return binding.root
    }

}