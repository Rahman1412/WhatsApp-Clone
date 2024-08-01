package com.example.whatsappclone.viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsappclone.models.ChatElement
import com.example.whatsappclone.models.Userdata
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ChatVM(val userId: String):ViewModel() {
    private val fb = Firebase.database
    private val _user = mutableStateOf(Userdata())
    val user = _user

    private val currentUser = Firebase.auth.currentUser

    private val _message = mutableStateOf(ChatElement())
    val message = _message

    val sender = mutableStateOf("")
    val receiver = mutableStateOf("")

    private val _chats = MutableStateFlow<List<ChatElement>>(emptyList())
    val chats : StateFlow<List<ChatElement>> = _chats

    private val active = mutableStateOf(false)
    init{
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                getUserById(userId)
                getChats(userId)
            }
        }
    }


    suspend fun clearChat(){
        val id = currentUser?.uid+"----------"+userId
        val ref = fb.getReference("chats/${id}").removeValue()
        fb.getReference("users/${userId}/time").removeValue()
    }


    private suspend fun getUserById(userId:String){
        val ref = fb.reference
        ref.child("users/${userId}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _user.value = snapshot.getValue(Userdata::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setValue(field:String,message:String){
        _message.value = _message.value.copy(
            message = message
        )
    }

    suspend fun upDateStatus(value:Boolean){
        val id = currentUser?.uid
        val ref = fb.getReference("users/${id}/online").setValue(value)
    }

     suspend fun getChats(userId: String){
        val thread = currentUser?.uid.toString() +"----------" +userId
        val ref = fb.getReference("chats/")
        ref.child(thread).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<ChatElement>()
                for (item in snapshot.children) {
                    item.getValue(ChatElement::class.java)?.let {
                        if(active.value) {
                            fb.getReference("chats/${thread}/${item.key}/read").setValue(true)
                        }
                        chatList.add(it)
                    }
                }
                _chats.value = chatList
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setActive(value:Boolean){
        active.value = value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(time:Long):String{
        val instant = Instant.ofEpochMilli(time)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val currentDate = LocalDate.now()

        return if (dateTime.toLocalDate() == currentDate) {
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
            dateTime.format(timeFormatter).uppercase(Locale.ENGLISH)
        } else {
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            dateTime.format(dateTimeFormatter)
        }
    }

    fun senMessage(userId:String){
        val time = System.currentTimeMillis()
        if(_message.value.message.trim() == ""){
            return
        }
        sender.value = currentUser?.uid.toString() +"----------" +userId
        receiver.value = userId+"----------"+currentUser?.uid.toString()
        val senderRef = fb.getReference("chats/${sender.value}")
        val receiverRef = fb.getReference("chats/${receiver.value}")
        val senderKey = senderRef.push().key
        val receiverKey = receiverRef.push().key

        updateFriendList()
        UpdateLastMessageAndTime(time)

        senderKey?.let{
            _message.value = _message.value.copy(
                time = time,
                send = true,
                read = true
            )
            senderRef.child(it).setValue(_message.value)
        }


        receiverKey?.let {
            _message.value = _message.value.copy(
                time = time,
                send = false,
                read = false
            )
            receiverRef.child(it).setValue(_message.value)
        }
        _message.value = _message.value.copy(message = "")
    }

    private fun UpdateLastMessageAndTime(time:Long){
        fb.getReference("users/${userId}/time").setValue(time)
        fb.getReference("users/${currentUser?.uid}/time").setValue(time)
    }

    private fun updateFriendList(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val senderFriend = fb.getReference("users")
                val receiverFriend = fb.getReference("users")

                senderFriend.child("${currentUser?.uid}/friends").get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        val friends = mutableListOf<String>()
                        for(friend in snapshot.children){
                            friends.add(friend.value.toString())
                        }
                        if(!friends.contains(userId)){
                            friends.add(userId)
                            senderFriend.child("${currentUser?.uid}/friends").setValue(friends)
                        }
                    }else{
                        val friends = listOf(userId)
                        senderFriend.child("${currentUser?.uid}/friends").setValue(friends)
                    }
                }

                receiverFriend.child("${userId}/friends").get().addOnSuccessListener { snapshot ->
                    if(snapshot.exists()){
                        val friends = mutableListOf<String>()
                        for(friend in snapshot.children){
                            friends.add(friend.value.toString())
                        }
                        if(!friends.contains(currentUser?.uid)){
                            friends.add(currentUser?.uid!!)
                            receiverFriend.child("${userId}/friends").setValue(friends)
                        }
                    }else{
                        val friends = listOf(currentUser?.uid)
                        receiverFriend.child("${userId}/friends").setValue(friends)
                    }
                }
            }
        }
    }

}