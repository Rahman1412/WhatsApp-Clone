package com.example.whatsappclone

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.navigation.RootNavHost
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.ui.theme.BlueTr
import com.example.whatsappclone.ui.theme.WhatsAppCloneTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.statusBarColor = BluePr.toArgb()
        window.navigationBarColor = BluePr.toArgb()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WhatsAppCloneTheme {
                RootNavHost()
            }
        }
    }
}