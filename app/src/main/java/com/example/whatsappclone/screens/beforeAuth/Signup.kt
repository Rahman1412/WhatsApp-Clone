package com.example.whatsappclone.screens.beforeAuth

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.whatsappclone.firebaseAuth.GoogleAuthUiClient
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.viewModel.AuthVM
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.sign

@Composable
fun Signup(rootNavController:NavController){
    val vm : AuthVM = viewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current.applicationContext
    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { activityResult ->
            if(activityResult.resultCode == Activity.RESULT_OK){
                runBlocking {
                    val signIntResult= googleAuthUiClient.signInWithIntent(
                        intent = activityResult.data ?: return@runBlocking
                    )
                    if(signIntResult.data != null){
                        vm.saveUser(signIntResult.data)
                        rootNavController.navigate(Graph.MainGraph){
                            popUpTo(Graph.AuthGraph){
                                inclusive = true
                            }
                        }
                    }else{
                        Toast.makeText(context,"Unable to authenticate, Please try after sometimes",Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Button(onClick = {
            if(rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
                lifecycleOwner.lifecycleScope.launch{
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            googleAuthUiClient.signIn() ?: return@launch
                        ).build()
                    )
                }
            }
        }) {
            Text(text = "Sign In")
        }
    }
}