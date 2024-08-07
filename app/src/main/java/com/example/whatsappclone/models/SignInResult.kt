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
    var time : Long? = null,
    val friends: List<String>? = null,
    val statusTime:Long? = null, )

data class ChatUser(
    val userId : String = "",
    val username : String = "",
    val email :String = "",
    val image : String = "",
    val online: Boolean = false,
    var message: String = "",
    var time : Long? = null,
    val friends: List<String>? = null,
    val statusTime:Long? = null,
    var read : Boolean? = null
)
