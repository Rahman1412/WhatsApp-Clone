package com.example.whatsappclone.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.screens.Splash
import com.example.whatsappclone.screens.afterAuth.Chat
import com.example.whatsappclone.screens.afterAuth.ChatBox
import com.example.whatsappclone.screens.afterAuth.Home
import com.example.whatsappclone.screens.afterAuth.NewChat
import com.example.whatsappclone.screens.afterAuth.StatusUpdates
import com.example.whatsappclone.screens.afterAuth.UserProfile
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavHost(){
    val rootController = rememberNavController()

    NavHost(
        navController = rootController,
        startDestination = Graph.RootGraph
    ){

        composable(Graph.RootGraph){
            Splash(rootController)
        }
        AuthGraphRoutes(rootController)
        composable(Graph.MainGraph){
            Home(rootController)
        }

        composable(Graph.NewChat){
            NewChat(rootController)
        }

        composable(Graph.ChatBox+"/{userId}"){ backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                ChatBox(rootController,userId)
            }
        }

        composable(Graph.UserProfile+"/{userId}"){backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                UserProfile(rootController,userId)
            }
        }

        composable(Graph.StatusUpdates+"/{userId}"){ backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId != null) {
                StatusUpdates(rootController,userId)
            }
        }
    }
}