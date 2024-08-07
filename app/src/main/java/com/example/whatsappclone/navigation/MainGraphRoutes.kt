package com.example.whatsappclone.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.whatsappclone.screens.afterAuth.Chat
import com.example.whatsappclone.screens.afterAuth.Profile
import com.example.whatsappclone.screens.afterAuth.Updates

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainGraphRoutes(
    rootNavController: NavController,
    navController: NavHostController,
    paddingValues: PaddingValues
    ){
    NavHost(navController = navController, startDestination = MainGraphRoute.Chat.route){
        composable(MainGraphRoute.Chat.route){
            Chat(rootNavController,paddingValues)
        }

        composable(MainGraphRoute.Updates.route) {
            Updates(rootNavController,paddingValues)
        }

        composable(MainGraphRoute.Profile.route) {
            Profile(navController,paddingValues)
        }

    }
}