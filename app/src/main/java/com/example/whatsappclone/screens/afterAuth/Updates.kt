package com.example.whatsappclone.screens.afterAuth

import androidx.compose.foundation.border
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.viewModel.StatusVM
import com.example.whatsappclone.viewModel.StatusVmFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun Updates(
    navController: NavController,
    paddingValues: PaddingValues
){
    val userId = Firebase.auth.currentUser?.uid!!
    val vm:StatusVM = viewModel(factory = StatusVmFactory(userId))

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
                ){
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Updates",
                        modifier = Modifier
                            .size(55.dp)
                            .border(
                                width = 4.dp,
                                color = BluePr,
                                shape = CircleShape
                            )
                    )
                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "Username", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "10:00 AM", fontSize = 12.sp)

                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ){
                Text(text = "Recent Updates", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ){
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Updates",
                        modifier = Modifier
                            .size(55.dp)
                            .border(
                                width = 4.dp,
                                color = BluePr,
                                shape = CircleShape
                            )
                    )
                    Column (
                        modifier = Modifier.padding(start = 8.dp)
                    ){
                        Text(text = "Username", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = "10:00 AM", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}