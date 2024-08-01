package com.example.whatsappclone.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.screens.beforeAuth.Login
import com.example.whatsappclone.screens.beforeAuth.Signup

fun NavGraphBuilder.AuthGraphRoutes(rootNavController: NavController) {
    navigation(
        route = Graph.AuthGraph,
        startDestination = AuthGraphRoute.Register.route
    ){
        composable(AuthGraphRoute.Login.route){
            Login(rootNavController)
        }
        composable(AuthGraphRoute.Register.route){
            Signup(rootNavController)
        }
    }
}