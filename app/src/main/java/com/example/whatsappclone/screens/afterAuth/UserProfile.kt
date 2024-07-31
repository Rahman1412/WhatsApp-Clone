package com.example.whatsappclone.screens.afterAuth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.whatsappclone.R
import com.example.whatsappclone.viewModel.ChatVM
import com.example.whatsappclone.viewModel.ChatVmFactory
import com.example.whatsappclone.viewModel.CronJob
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun UserProfile(rootController:NavController,userId:String){
    val vm : ChatVM = viewModel(factory = ChatVmFactory(userId))
    val user = vm.user.value
    val cron : CronJob = viewModel()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 10.dp, end = 10.dp)
    ){
        Column {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 25.dp),
                horizontalArrangement = Arrangement.Center
            ){
                AsyncImage(
                    model = user.image,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(160.dp)
                )
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 40.dp)
            ){
                Row(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.user),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column (
                    modifier = Modifier.padding(start = 20.dp)
                ){
                    Text(text = "Name", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = user.username)
                }
            }



            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 40.dp)
            ){
                Row(
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Icon(
                        Icons.Filled.Call,
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column (
                    modifier = Modifier.padding(start = 20.dp)
                ){
                    Text(text = "Email", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text(text = user.email)
                }
            }
        }
    }
}