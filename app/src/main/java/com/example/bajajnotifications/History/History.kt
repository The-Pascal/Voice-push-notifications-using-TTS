package com.example.bajajnotifications.History

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bajajnotifications.databinding.ActivityHistoryBinding
import com.example.bajajnotifications.db.AppDatabase
import com.example.bajajnotifications.db.Notifications


class History : AppCompatActivity() {

    private val db by lazy{
        AppDatabase.getDatabase(this)
    }

    private val list = arrayListOf<Notifications>()
    private var adapter = HistoryAdapter(list)

    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.notifRv.apply {
            layoutManager = LinearLayoutManager(this@History)
            adapter = this@History.adapter
        }

        displayNotifications()
    }

    private fun displayNotifications() {
        db.notificationDao().getNotifications().observe(this, Observer {
            if(!it.isNullOrEmpty()){
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
            else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })
    }
}