package com.example.whatsappclone.screens.afterAuth

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.example.whatsappclone.navigation.MainGraphRoute
import com.example.whatsappclone.ui.theme.BluePr
import com.example.whatsappclone.viewModel.CronJob
import com.example.whatsappclone.viewModel.StatusVM
import com.example.whatsappclone.viewModel.StatusVmFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatusUpdates(
    rootNavController: NavController,
    userId:String
) {
    val cron : CronJob = viewModel()
    val context = LocalContext.current.applicationContext
    val application = context.applicationContext as Application
    val vm : StatusVM = viewModel(factory = StatusVmFactory(application,userId))
    val size = vm.size.collectAsState().value
    val status = vm.myStatus.collectAsState().value


    val progressList = remember { mutableStateListOf<Animatable<Float, *>>() }
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current.density
    val screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val currentProgressIndex = remember { mutableStateOf(0) }
    val currentUser = vm.currentUser.value

    fun startAnimation(index: Int,duration:Int = 3000) {
        scope.launch {
            progressList.getOrNull(index)?.let { progress ->
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = duration,
                        easing = LinearEasing
                    )
                )
                if (index + 1 < progressList.size) {
                    currentProgressIndex.value = index + 1
                    startAnimation(currentProgressIndex.value, 3000)
                }else{
                    if(rootNavController.currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED){
                        rootNavController.navigateUp()
                    }
                }
            }
        }
    }

    fun clearAnimation(index: Int) {
        scope.launch {
            progressList.getOrNull(index)?.let { progress ->
                progress.animateTo(
                    targetValue = 0f
                )
                currentProgressIndex.value = index -1
            }

            if(currentProgressIndex.value >= 0){
                progressList.getOrNull(index - 1)?.let { progress ->
                    progress.animateTo(
                        targetValue = 0f
                    )
                    if(currentProgressIndex.value != 0){
                        currentProgressIndex.value = index -1
                    }
                }
            }
            startAnimation(currentProgressIndex.value,3000)
        }
    }

    LaunchedEffect(size) {
        startAnimation(currentProgressIndex.value,3000)

        if(size > 0 && size == status.size){
            repeat(size) {
                progressList.add(Animatable(0f))
            }
        }
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val clickX = offset.x
                    if (clickX > screenWidthPx / 2) {
                        startAnimation(currentProgressIndex.value, 50)
                    } else {
                        if (currentProgressIndex.value > 0) {
                            clearAnimation(currentProgressIndex.value)
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 46.8.dp)
        ){
            Row {
                progressList.forEachIndexed { index, progress ->
                    LinearProgressIndicator(
                        progress = progress.value,
                        color = BluePr,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(5.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .weight(1f)
                            .padding(start = 2.dp, end = 2.dp)
                    )
                }
            }
            if(size > 0) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        AsyncImage(
                            model = currentUser.image,
                            contentDescription = "Image",
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        )
                        Column (
                            modifier = Modifier.padding(start = 10.dp)
                        ){
                            Text(
                                text = currentUser.username,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            status[currentProgressIndex.value].time?.let {
                                Text(
                                    text = vm.getTime(it),
                                    color = Color.White
                                )
                            }
                        }
                    }
                    if (currentProgressIndex.value in status.indices) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                model = status[currentProgressIndex.value].url,
                                contentDescription = "Image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(15.dp)
                            )
                        }
                    }
            }
        }
    }
}
