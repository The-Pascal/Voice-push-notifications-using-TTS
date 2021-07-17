package com.example.bajajnotifications.history

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bajajnotifications.databinding.HistoryFragmentBinding
import com.example.bajajnotifications.db.AppDatabase
import com.example.bajajnotifications.db.Notifications

class HistoryFragment : Fragment() {

    val db by lazy{
        context?.let { AppDatabase.getDatabase(it) }
    }

    private val list = arrayListOf<Notifications>()
    private var adapter = HistoryAdapter(list)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = HistoryFragmentBinding.inflate(inflater)
        val viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        binding.notifRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@HistoryFragment.adapter
        }

        displayNotifications()

        return binding.root
    }

    private fun displayNotifications() {
        db?.notificationDao()?.getNotifications()?.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            } else {
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }

}