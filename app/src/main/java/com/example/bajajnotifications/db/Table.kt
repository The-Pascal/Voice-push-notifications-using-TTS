package com.example.bajajnotifications.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Notifications(
    var title : String,
    var description : String,
    var timestamp : Long = System.currentTimeMillis(),
    var imageUri : String,
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
)