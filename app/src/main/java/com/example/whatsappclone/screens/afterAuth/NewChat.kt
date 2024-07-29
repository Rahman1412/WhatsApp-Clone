package com.example.whatsappclone.screens.afterAuth

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whatsappclone.screens.components.Users
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.ui.theme.BlueSc
import com.example.whatsappclone.ui.theme.BlueTr
import com.example.whatsappclone.viewModel.AuthVM

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChat(rootNavController: NavController){
    val vm : AuthVM = viewModel()
    val users = vm.users.collectAsState().value
    var isEnable by rememberSaveable {
        mutableStateOf(true)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ){
        Row {
            TopAppBar(
                title = {
                    Text(text = "New Chat", fontWeight = FontWeight.Bold, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                            rootNavController.navigateUp()
                        isEnable = false
                    },
                        enabled = isEnable) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(BluePr)
            )
        }
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(bottom = 60.dp)
        ){
            if(vm.loading.value){
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    color = BluePr
                )
            }else if(users.isEmpty() && !vm.loading.value){
                Text(
                    text = "No User Found",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp),
                    color = Color.Red
                )
            }else{
                users.forEach {
                    Users(it,rootNavController,false)
                }
            }
        }
    }
}