package com.example.whatsappclone.models

import androidx.core.app.NotificationCompat.MessagingStyle.Message

data class ChatElement(
    val message: String = "",
    val send:Boolean  = false,
    val time : Long = 0,
    val read:Boolean = false
)

