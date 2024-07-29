package com.example.whatsappclone.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.ui.theme.BlueTr
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Splash(rootController: NavController){
    val authUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var route by remember { mutableStateOf(Graph.AuthGraph) }
    Log.d("Auth User","${authUser}")
    authUser?.let { route = Graph.MainGraph }

    LaunchedEffect(Unit) {
        delay(2000)
        rootController.navigate(route){
            popUpTo(Graph.RootGraph){
                inclusive = true
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BluePr),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Splash", color = Color.White)
    }
}