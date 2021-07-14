package com.example.bajajnotifications.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDao{

    @Insert
    suspend fun insertNotification( Notification : Notifications ) : Long

    @Query(" Select * From Notifications Order By id DESC ")
    fun getNotifications() : LiveData< List<Notifications> >
}