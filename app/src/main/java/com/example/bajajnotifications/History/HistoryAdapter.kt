package com.example.bajajnotifications.History

import android.app.Notification
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bajajnotifications.R
import com.example.bajajnotifications.databinding.NotificationBinding
import com.example.bajajnotifications.db.Notifications

class HistoryAdapter(val list : List<Notifications>) : RecyclerView.Adapter<HistoryAdapter.historyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): historyViewHolder {
        return historyViewHolder( LayoutInflater.from(parent.context).inflate(R.layout.notification, parent,false) )
    }

    override fun onBindViewHolder(holder: historyViewHolder, position: Int) {
        holder.bind( list[position] )
    }

    override fun getItemCount() = list.size

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    class historyViewHolder( itemView : View) : RecyclerView.ViewHolder( itemView ){

        private val binding = NotificationBinding.bind(itemView)
            //for now i am displaying just title and description
            fun bind( notification : Notifications ){
                with( binding ){
                    notificationTitle.text = notification.title
                    notificationDescription.text = notification.description
                }
            }
    }


}