package com.example.whatsappclone.screens.afterAuth

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.whatsappclone.R
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.viewModel.CronJob
import com.example.whatsappclone.viewModel.StatusVM
import com.example.whatsappclone.viewModel.StatusVmFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Updates(
    rootNavController: NavController,
    paddingValues: PaddingValues
){
    var userId :String = ""
    if(Firebase.auth.currentUser != null){
        userId = Firebase.auth.currentUser?.uid!!
    }
    val context = LocalContext.current.applicationContext
    val application = context as Application

    val vm : StatusVM = viewModel(factory = StatusVmFactory(application,userId))

    val currentUser = vm.currentUser.value
    val allUsers = vm.allUsers.collectAsState().value
    val myStatusSize = vm.size.collectAsState().value

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val cron : CronJob = viewModel()
    var toast : Toast? = null
    if(vm.progress.value == true){
        toast?.cancel()
        toast = Toast.makeText(context,"Sending status update...",Toast.LENGTH_LONG)
        toast.show()
        vm.setFalse()
    }else if(vm.processed.value == true){
        toast?.cancel()
        Toast.makeText(context,"Status sent",Toast.LENGTH_SHORT).show()
        vm.setFalse()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let {
            vm.uploadStatus(it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let{
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path = MediaStore.Images.Media.insertImage(context.contentResolver,it, "Title", null)
            Uri.parse(path.toString())?.let{
                vm?.uploadStatus(it)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ){
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(15.dp)
        ){
            Column{
                Text(
                    text = "Status",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable {
                            if (myStatusSize > 0) {
                                rootNavController.navigate(Graph.StatusUpdates + "/" + userId) {
                                    launchSingleTop = true
                                }
                            }else{
                                showBottomSheet = true
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Box(
                        modifier =
                        Modifier.border(
                            width = if(myStatusSize > 0 )3.dp else 0.dp,
                            color = if(myStatusSize > 0) BluePr else Color.White,
                            shape = CircleShape
                        )
                    ){
                        AsyncImage(
                            model = currentUser.image,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .padding(3.dp)
                                .size(50.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        )
                        if(myStatusSize == 0){
                            Icon(
                                Icons.Filled.AddCircle,
                                contentDescription = "Add Status",
                                tint = BluePr,
                                modifier = Modifier.align(Alignment.Center).size(50.dp)
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(text = "My Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        currentUser.statusTime?.let{
                            Text(text = vm.getTime(it), fontSize = 12.sp)
                        }
                    }
                }
            }

            if(allUsers?.size!! > 0){
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ){
                    Text(text = "Recent Updates", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 10.dp))
                    allUsers.forEach { it ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                                .clickable {
                                    if (rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                                        rootNavController.navigate(Graph.StatusUpdates + "/" + it.userId) {
                                            launchSingleTop = true
                                        }
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.border(
                                    width = 3.dp,
                                    color = BluePr,
                                    shape = CircleShape
                                )
                            ){
                                AsyncImage(
                                    model = it.image,
                                    contentDescription = "Profile",
                                    modifier = Modifier
                                        .padding(3.dp)
                                        .size(50.dp)
                                        .clip(CircleShape)
                                        .border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                )
                            }
                            Column(
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text(text = it.username, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                it.statusTime?.let{
                                    Text(text = vm.getTime(it), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }


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