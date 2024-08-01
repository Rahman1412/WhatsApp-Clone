package com.example.whatsappclone.screens.afterAuth

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.whatsappclone.R
import com.example.whatsappclone.navigation.Graph
import com.example.whatsappclone.screens.LifecycleAware
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.ui.theme.BlueTr
import com.example.whatsappclone.viewModel.ChatVM
import com.example.whatsappclone.viewModel.ChatVmFactory
import com.example.whatsappclone.viewModel.CronJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("CoroutineCreationDuringComposition", "UnusedMaterial3ScaffoldPaddingParameter",
    "UnrememberedMutableState"
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatBox(rootNavController: NavController,userId:String) {
    val vm: ChatVM = viewModel(factory = ChatVmFactory(userId))
    val coroutine = rememberCoroutineScope()
    val user = vm.user.value
    val chats = vm.chats.collectAsState().value
    val scrollState = rememberScrollState()
    val cron : CronJob = viewModel()

    var isExpand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(chats.size) {
        withContext(Dispatchers.IO){
            scrollState.animateScrollTo(scrollState.maxValue)
            vm.setActive(true)
            vm.getChats(userId)
        }
    }

    LifecycleAware {source,event ->
        when(event){
            Lifecycle.Event.ON_DESTROY -> {
                println("Hello World Destroy")
            }

            Lifecycle.Event.ON_CREATE -> {
            }
            Lifecycle.Event.ON_START -> {
                coroutine.launch {
                    withContext(Dispatchers.IO){
                        vm.upDateStatus(true)
                    }
                }

            }
            Lifecycle.Event.ON_RESUME -> {
            }
            Lifecycle.Event.ON_PAUSE -> {
            }
            Lifecycle.Event.ON_STOP -> {
                coroutine.launch {
                    withContext(Dispatchers.IO){
                        vm.setActive(false)
                        vm.upDateStatus(false)
                    }
                }
            }
            Lifecycle.Event.ON_ANY -> {
                println("Hello World ANY")
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding().fillMaxHeight(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = user.image,
                            contentDescription = "User",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape).clickable {
                                    rootNavController.navigate(Graph.UserProfile+"/"+userId){
                                        launchSingleTop = true
                                    }
                                }
                        )
                        Column(
                            modifier = Modifier.padding(start = 5.dp)
                        ) {
                            Text(
                                text = user.username,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if(user.online){
                                Text(
                                    text = "Online",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if(rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
                            rootNavController.navigateUp()
                        }
                    }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                      IconButton(onClick = {
                          isExpand = true
                      }) {
                          Icon(Icons.Filled.MoreVert, contentDescription = "Clear Chat", tint = Color.White)
                      }
                    DropdownMenu(expanded = isExpand, onDismissRequest = {
                        isExpand = false
                    }) {
                        DropdownMenuItem(
                            text = { 
                                   Text(text = "Clear Chat")
                            },
                            onClick = {
                                coroutine.launch {
                                    withContext(Dispatchers.IO){
                                        vm.clearChat()
                                        isExpand = false
                                    }
                                }
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(BluePr),
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(top = 60.dp)
                    .systemBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp)
                        .weight(1f)
                        .verticalScroll(scrollState)
                ) {
                chats.forEach { chat ->
                    if(chat.send){
                        Card(
                            modifier = Modifier
                                .align(Alignment.End)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(color = BlueTr)
                                    .padding(5.dp)
                                    .widthIn(min = 0.dp, max = 300.dp)
                                    .wrapContentWidth()
                            ) {
                                Text(text = chat.message)
                                Text(
                                    text = vm.getTime(chat.time),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }else{
                        Card(
                            modifier = Modifier
                                .align(Alignment.Start)
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(color = Color.LightGray)
                                    .padding(5.dp)
                                    .widthIn(min = 0.dp, max = 300.dp)
                                    .wrapContentWidth()
                            ) {
                                Text(text = chat.message)
                                Text(
                                    text = vm.getTime(chat.time),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }

                }
                Row(
                    modifier = Modifier
                        .imePadding()
                        .padding(start = 10.dp, end = 10.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    OutlinedTextField(
                        value = vm.message.value.message,
                        onValueChange = {
                            vm.setValue("message", it)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(25.dp),
                        textStyle = TextStyle(
                            fontSize = 14.sp, // Adjust text size
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    IconButton(
                        onClick = { vm.senMessage(userId) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(45.dp)
                    ) {
                        Icon(
                            Icons.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BluePr)
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    )
}