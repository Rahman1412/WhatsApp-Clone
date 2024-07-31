package com.example.whatsappclone.viewModel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Build
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.snap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.whatsappclone.models.Status
import com.example.whatsappclone.models.UpdateStatus
import com.example.whatsappclone.models.Userdata
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class StatusVM(application: Application,userId:String): AndroidViewModel(application){
    val store = Firebase.firestore
    val ref: StorageReference = FirebaseStorage.getInstance().getReference()
    val db = Firebase.database
    val status = mutableStateOf<Status?>(null)
    val userId = userId
    val myStatus = MutableStateFlow<List<Status>>(emptyList())
    val size = MutableStateFlow(0)

    val currentUser = mutableStateOf(Userdata())

    val allUsers = MutableStateFlow<List<Userdata>?>(emptyList())
    val progress = mutableStateOf(false)
    val processed = mutableStateOf(false)
    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                getCurrentUser()
                getMyUpdates()
                getAllUsers()
            }
        }
    }

    suspend fun getAllUsers(){
        db.getReference("status").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val users = mutableListOf<Userdata>()
                    for(item in snapshot.children){
                        if(item.key != userId){
                             db.getReference("users/${item.key}").get().addOnSuccessListener { user ->
                                if(user.exists()){
                                    user.getValue(Userdata::class.java)?.let {
                                        users.add(it)
                                        allUsers.value = users
                                    }
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


     suspend fun getCurrentUser(id : String = userId){
        val ref = db.getReference("users")
        ref.child(id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.getValue(Userdata::class.java)?.let { user ->
                        db.getReference("status/${user.userId}").orderByKey().limitToLast(1).get().addOnSuccessListener {status->
                            if(status.exists()){
                                for(s in status.children){
                                    s.getValue(Status::class.java)?.let{
                                        currentUser.value = user
                                    }
                                }
                            }else{
                                currentUser.value = user
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    suspend fun getMyUpdates(){
        val ref = db.getReference("status/${userId}").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    size.value = snapshot.childrenCount.toInt()
                    val status = mutableListOf<Status>()
                    for (item in snapshot.children){
                        item.getValue(Status::class.java)?.let{
                            status.add(it)
                            myStatus.value= status
                        }
                    }
                }else{
                    myStatus.value= emptyList()
                    size.value = 0
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setFalse(){
        progress.value = false
        processed.value = false
    }

    fun uploadStatus(uri:Uri){
        progress.value = true
        runBlocking {
            withContext(Dispatchers.IO) {
                val name = "${System.currentTimeMillis()}.${getFileExtension(uri)}"
                val storageRef = ref.child(name)
                val time = System.currentTimeMillis()
                storageRef.putFile(uri).addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { url ->
                        val statusRef = db.getReference("status/${userId}")
                        val key = statusRef.push().key
                        key?.let {
                            statusRef.child(it).setValue(
                                Status(
                                    url = url.toString(),
                                    time = time
                                )
                            )
                            progress.value = false
                            processed.value = true
                            db.getReference("users/${userId}/statusTime").setValue(time)
                        }
                    }
                }.addOnFailureListener {
                }
            }
        }
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
            val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a")
            dateTime.format(dateTimeFormatter).uppercase(Locale.ENGLISH)
        }
    }

    fun getFileExtension(uri:Uri?) : String? {
        val contentResolver : ContentResolver = getApplication<Application>().contentResolver
        val mime : MimeTypeMap? = MimeTypeMap.getSingleton()
        return mime?.getExtensionFromMimeType(uri?.let { contentResolver.getType(it) })
    }

}