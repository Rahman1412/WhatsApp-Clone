package com.example.whatsappclone.screens.afterAuth

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.navigation.MainGraphRoute
import com.example.whatsappclone.navigation.MainGraphRoutes
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.ui.theme.BlueSc
import com.example.whatsappclone.ui.theme.BlueTr
import com.example.whatsappclone.viewModel.StatusVM
import com.example.whatsappclone.viewModel.StatusVmFactory
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    rootController: NavController,
){
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val backStackEntry = navController.currentBackStackEntryAsState()
    var title by rememberSaveable {
        mutableStateOf("WhatsApp")
    }
    var isExpand by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }


    val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = context,
            oneTapClient = Identity.getSignInClient(context)
        )
    }

    val bottomNavItem = listOf(
        BottomNavBar(name = "Chat", route = MainGraphRoute.Chat.route, icon = R.drawable.chat),
        BottomNavBar(name = "Updates", route = MainGraphRoute.Updates.route,icon = R.drawable.updates),
        BottomNavBar(name = "Profile", route = MainGraphRoute.Profile.route, icon = R.drawable.user)
    )

    val application = context as Application

    var updateVm: StatusVM? = null
    if(Firebase.auth.currentUser != null) {
         updateVm = viewModel(factory = StatusVmFactory(application,
            Firebase.auth.currentUser?.uid!!))
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let {
            scope.launch {
                withContext(Dispatchers.IO){
                    updateVm?.uploadStatus(it)
                }
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) {

    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePr,
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(title, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                },
                actions = {
                    IconButton(onClick = {
                        isExpand = true
                    }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(expanded = isExpand, onDismissRequest = {
                        isExpand = false
                    }) {
                        DropdownMenuItem(
                            text = { 
                                   Text(text = "Log Out")
                            }, 
                            onClick = {
                                isExpand = false
                                googleAuthUiClient.signOut()
                                rootController.navigate(Graph.AuthGraph){
                                    popUpTo(Graph.MainGraph){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BluePr,
                contentColor = Color.White,
            ) {
                bottomNavItem.forEach { navItem ->
                    val selected = navItem.route == backStackEntry.value?.destination?.route
                    val color = if(selected) Color.Black else Color.White


                    NavigationBarItem(
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(BlueSc),
                        onClick = {
                            navController.navigate(navItem.route){
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            title = if(navItem.name == "Chat") "WhatsApp" else navItem.name
                        },
                        icon = {
                            navItem.icon?.let { Icon(painter = painterResource(it), contentDescription = navItem.name,modifier = Modifier.size(18.dp), tint = color ) }
                        },
                        label = {
                            Text(text = navItem.name, color = BlueTr, fontWeight = FontWeight.SemiBold)
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if(title != "Profile"){
                FloatingActionButton(
                    onClick = {
                        if(title == "WhatsApp"){
                            rootController.navigate(Graph.NewChat){
                                launchSingleTop = true
                            }
                        }else{
                            showBottomSheet = true
                        }
                    },
                    containerColor = BluePr,
                    contentColor = Color.White
                ) {
                    Icon(painter = painterResource(if(title == "WhatsApp") R.drawable.newchat else R.drawable.camera), contentDescription = "Add", modifier = Modifier.size(22.dp))
                }
            }
        }
    ){ innerPadding ->
        MainGraphRoutes(rootController,navController,innerPadding)

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Row{
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(50.dp)
                            .clickable {
                                scope
                                    .launch {
                                        withContext(Dispatchers.IO) {
                                            cameraLauncher.launch()
                                            sheetState.hide()
                                        }
                                    }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                            },
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.cam),
                            contentDescription = "Camera"
                        )
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(50.dp)
                            .clickable {
                                scope
                                    .launch {
                                        withContext(Dispatchers.IO) {
                                            galleryLauncher.launch("image/*")
                                            sheetState.hide()
                                        }
                                    }
                                    .invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            showBottomSheet = false
                                        }
                                    }
                            },
                        colors = CardDefaults.cardColors(Color.White)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.gallery),
                            contentDescription = "Camera"
                        )
                    }
                }
            }
        }
    }
}