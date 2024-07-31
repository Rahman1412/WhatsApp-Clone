package com.example.whatsappclone.screens.afterAuth

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.whatsappclone.R
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.models.BottomNavBar
import com.example.whatsappclone.navigation.MainGraphRoute
import com.example.whatsappclone.navigation.MainGraphRoutes
import com.example.whatsappclone.screens.components.Users
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.viewModel.AuthVM
import com.example.whatsappclone.viewModel.CronJob
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.WatchEvent

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    rootNavController: NavController,
    paddingValues: PaddingValues
){
    val vm : AuthVM = viewModel()
    val users = vm.chatUsers.collectAsState().value
    val cron : CronJob = viewModel()
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO){
            vm.getMyChats()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(color = Color.White)
    ){
        if(vm.loading.value){
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = BluePr
            )
        }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = 10.dp)
        ){
            users.forEach {
                Users(it, rootNavController,true)
            }
        }
    }
}

