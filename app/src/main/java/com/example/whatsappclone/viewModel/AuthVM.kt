package com.example.whatsappclone.viewModel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.snap
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.models.ChatElement
import com.example.whatsappclone.models.SignInResult
import com.example.whatsappclone.models.Userdata
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.snapshots
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
import java.util.Objects

class AuthVM(application: Application):AndroidViewModel(application) {

    private val db = Firebase.database
    private var _users : MutableStateFlow<List<Userdata>> = MutableStateFlow(emptyList())
    val users : StateFlow<List<Userdata>> = _users

    private val _myChatUser : MutableStateFlow<List<Userdata>> = MutableStateFlow(emptyList())
    val chatUsers : StateFlow<List<Userdata>> = _myChatUser

    private val currentUser = Firebase.auth.currentUser

    private val _loading = mutableStateOf(true)
    val loading = _loading

    init{
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                getAllUsers()
            }
        }
    }

    suspend fun getMyChats(){
        val id = Firebase.auth.currentUser?.uid
        db.getReference("users/${id}").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val ref = db.getReference("users/${id}/friends").addValueEventListener(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        _loading.value = true
                        val myfriends = mutableListOf<Userdata>()
                        if(snapshot.exists()){
                            for(item in snapshot.children){
                                db.getReference("users/${item.value.toString()}").get().addOnSuccessListener { snap ->
                                    if(snap.exists()){
                                        snap.getValue(Userdata::class.java)?.let { user->
                                            val index = myfriends.indexOfFirst { it.userId == user.userId }
                                            if(index != -1){
                                                myfriends[index] = user
                                            }else{
                                                myfriends.add(user)
                                            }
                                            _myChatUser.value = myfriends.sortedByDescending { it.time }
                                            _loading.value = false
                                        }
                                    }else{
                                        _loading.value = true
                                    }
                                }
                            }
                        }else{
                            _loading.value = false
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
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

    private suspend fun getAllUsers(){
        val ref = db.reference

        ref.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<Userdata>()
                for (user in snapshot.children) {
                    if(user.key != currentUser?.uid) {
                        user.getValue(Userdata::class.java)?.let {
                            userList.add(it)
                            _users.value = userList
                            _loading.value = false
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun saveUser(user:Userdata){
        db.getReference("users/${user.userId}").get().addOnSuccessListener { snapshot ->
            if(!snapshot.exists()) {
                db.getReference("users/${user.userId}").setValue(user)
            }
        }
    }

    fun doSignIn(result:SignInResult){
        println(result)
    }

}