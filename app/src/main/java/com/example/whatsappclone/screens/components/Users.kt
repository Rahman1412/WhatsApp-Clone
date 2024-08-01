package com.example.whatsappclone.screens.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.whatsappclone.R
import com.example.whatsappclone.models.ChatUser
import com.example.whatsappclone.models.Userdata
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.viewModel.AuthVM
import com.example.whatsappclone.viewModel.ChatVM

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Users(user:ChatUser,rootNavController: NavController,isDisplay:Boolean = false){
    val vm : AuthVM = viewModel()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                rootNavController.navigate(Graph.ChatBox + "/" + user.userId) {
                    launchSingleTop = true
                }
            },
        colors = CardDefaults.cardColors(Color.White)
    ){
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model = user.image,
                contentDescription = "User",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(50.dp)
            )
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = user.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if(isDisplay){
                        user.time?.let {
                            if(it > 0){
                                Text(text = vm.getTime(user.time!!), fontSize = 12.sp)
                            }
                        }
                    }

                }
                if(user.message != "" && isDisplay){
                    Text(text = user.message,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if(user.read == true) FontWeight.Normal else FontWeight.Bold
                    )
                }
            }
        }
    }
}