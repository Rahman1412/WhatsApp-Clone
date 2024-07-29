package com.example.whatsappclone.models

import androidx.core.app.NotificationCompat.MessagingStyle.Message

data class SignInResult(
    val data : Userdata?,
    val error : String?
)

data class Userdata(
    val userId : String = "",
    val username : String = "",
    val email :String = "",
    val image : String = "",
    val online: Boolean = false,
    val message: String = "",
    val time : Long? = null,
    val friends: List<String>? = null
)
