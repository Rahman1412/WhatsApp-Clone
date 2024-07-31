package com.example.whatsappclone.viewModel

import androidx.compose.animation.core.snap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.models.Status
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CronJob : ViewModel() {
    val db = Firebase.database

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                clearStatus()
            }
        }
    }

    suspend fun clearStatus(){
        val ref = db.getReference("status").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(user in snapshot.children){
                        checkUser(user.key.toString())
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun checkUser(userId:String){
        db.getReference("status/${userId}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val child = snapshot.childrenCount
                    for(item in snapshot.children){
                        item.getValue(Status::class.java)?.let{
                            if(is24HoursOld(it.time!!)){
                                db.getReference("status/${userId}/${item.key}").removeValue()
                                if(child.toInt() == 1){
                                    db.getReference("users/${userId}/statusTime").removeValue()
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun is24HoursOld(timestamp: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val difference = currentTime - timestamp
        val twentyFourHoursInMillis = 24 * 60 * 60 * 1000
        return difference >= twentyFourHoursInMillis
    }
}