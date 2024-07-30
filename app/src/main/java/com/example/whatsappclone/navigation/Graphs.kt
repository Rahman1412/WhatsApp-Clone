package com.example.whatsappclone.navigation

object Graph {
    const val RootGraph = "root"
    const val AuthGraph = "auth"
    const val MainGraph = "home"
    const val NewChat = "new-chat"
    const val ChatBox = "chatbox"
    const val UserProfile = "user-profile"
    const val StatusUpdates = "status-updates"
}

sealed class AuthGraphRoute(val route:String){
    object Login : AuthGraphRoute("login")
    object Register : AuthGraphRoute("register")
}

sealed class MainGraphRoute(val route:String){
    object Home: MainGraphRoute("home")
    object Chat: MainGraphRoute("chat")
    object Updates: MainGraphRoute("updates")
    object Profile: MainGraphRoute("profile")
}