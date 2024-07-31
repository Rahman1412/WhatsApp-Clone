package com.example.whatsappclone.viewModel

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whatsappclone.models.Status
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
import kotlinx.coroutines.withContext
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
        val ref = db.getReference("status").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val users = mutableListOf<Userdata>()
                    for(item in snapshot.children){
                        if(item.key != userId){
                            val userRef = db.getReference("users/${item.key}").get().addOnSuccessListener { snapshot ->
                                if(snapshot.exists()){
                                    snapshot.getValue(Userdata::class.java)?.let{
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
                    snapshot.getValue(Userdata::class.java)?.let {
                        currentUser.value = it
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    suspend fun getMyUpdates(){
        val ref = db.getReference("status")
        ref.child(userId).addValueEventListener(object : ValueEventListener{
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
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    suspend fun uploadStatus(uri:Uri){
        val name = "${System.currentTimeMillis()}.${getFileExtension(uri)}"
        val storageRef = ref.child(name)
        storageRef.putFile(uri).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { url ->
                status.value = status.value?.copy(
                    url = url.toString(),
                    time = System.currentTimeMillis()
                )
                val statusRef = db.getReference("status/${userId}")
                val key = statusRef.push().key
                key?.let{
                    statusRef.child(it).setValue(status.value)
                    status.value = Status()
                }

            }
        }.addOnFailureListener{
        }
    }

    fun getFileExtension(uri:Uri?) : String? {
        val contentResolver : ContentResolver = getApplication<Application>().contentResolver
        val mime : MimeTypeMap? = MimeTypeMap.getSingleton()
        return mime?.getExtensionFromMimeType(uri?.let { contentResolver.getType(it) })
    }

}