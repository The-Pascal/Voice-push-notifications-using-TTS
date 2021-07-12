package com.example.bajajnotifications.History

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bajajnotifications.databinding.ActivityHistoryBinding
import com.example.bajajnotifications.db.Notifications


class History : AppCompatActivity() {

    val list = arrayListOf<Notifications>()
    var adapter = HistoryAdapter(list)

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

//        list.add(Notifications("First Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Second Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Third Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Fourth Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Fifth Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Sixth Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Seventh Notification", "Sample Description", "xyz", "xyz"))
//        list.add(Notifications("Eighth Notification", "Sample Description", "xyz", "xyz"))

    }
}