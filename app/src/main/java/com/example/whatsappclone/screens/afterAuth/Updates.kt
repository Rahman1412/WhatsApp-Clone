package com.example.whatsappclone.screens.afterAuth

import android.app.Application
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.viewModel.StatusVM
import com.example.whatsappclone.viewModel.StatusVmFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Updates(
    rootNavController: NavController,
    paddingValues: PaddingValues
){
    val userId = Firebase.auth.currentUser?.uid!!
    val context = LocalContext.current.applicationContext
    val application = context as Application

    val vm : StatusVM = viewModel(factory = StatusVmFactory(application,userId))

    val currentUser = vm.currentUser.value
    val allUsers = vm.allUsers.collectAsState().value
    val myStatusSize = vm.size.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(15.dp)
        ){
            Column{
                Text(
                    text = "Status",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            if (myStatusSize > 0) {
                                rootNavController.navigate(Graph.StatusUpdates + "/" + userId) {
                                    launchSingleTop = true
                                }
                            }
                        }
                ){
                    AsyncImage(
                        model = currentUser.image,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(55.dp)
                            .clip(CircleShape)
                            .border(
                                width = 2.dp,
                                color = BluePr,
                                shape = CircleShape
                            )
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "My Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "10:00 AM", fontSize = 12.sp)

                    }
                }
            }

            if(allUsers?.size!! > 0){
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ){
                    Text(text = "Recent Updates", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))
                    allUsers.forEach { it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable {
                                    if(rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
                                        rootNavController.navigate(Graph.StatusUpdates + "/" + it.userId) {
                                            launchSingleTop = true
                                        }
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = it.image,
                                contentDescription = "Updates",
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 4.dp,
                                        color = BluePr,
                                        shape = CircleShape
                                    )
                            )
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = it.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text(text = "10:00 AM", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}