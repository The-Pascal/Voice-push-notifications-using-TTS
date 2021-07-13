package com.example.bajajnotifications.dataModels

data class ReceivedNotification(
    var title: String,
    var body: String,
    var imageUrl: String
) {
    constructor(): this("","","")
}