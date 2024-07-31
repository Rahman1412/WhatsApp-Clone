package com.example.whatsappclone.screens.beforeAuth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.navigation.AuthGraphRoute
import com.example.whatsappclone.viewModel.CronJob
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.runBlocking

@Composable
fun Login(rootNavController: NavController){
    val cron : CronJob = viewModel()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Login")
            ClickableText(text = AnnotatedString("Don't Have An Account? Sign Up"), onClick = {
                if(rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                    rootNavController.navigate(AuthGraphRoute.Register.route)
                }
            })
        }
    }
}