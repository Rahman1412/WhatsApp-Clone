package com.example.whatsappclone.service

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.whatsappclone.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFcmService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCM Notification"
        const val DEFAULT_NOTIFICATION_ID = 0
    }

    override fun onNewToken(token: String) {
        Log.d("TOken", "${token}")
        val notification = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        initNotificationChannel(notification)
    }

    private fun initNotificationChannel(notificationManager : NotificationManager) {
        if(Build.VERSION_CODES.O <= Build.VERSION.SDK_INT){
            notificationManager.createNotificationChannelIfNotExists(
                channelId = "1",
                channelName = "Default"
            )
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(!isAppInForeground()){
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            var notificationBuilder = if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
                NotificationCompat.Builder(applicationContext, "1")
            } else {
                NotificationCompat.Builder(applicationContext)
            }
            notificationBuilder = notificationBuilder
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
            initNotificationChannel(notificationManager)
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = packageName
        return runningAppProcesses.any { appProcess ->
            appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName == packageName
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun NotificationManager.createNotificationChannelIfNotExists(
        channelId: String,
        channelName: String,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        var channel = this.getNotificationChannel(channelId)

        if (channel == null) {
            channel = NotificationChannel(
                channelId,
                channelName,
                importance
            )
            this.createNotificationChannel(channel)
        }
    }
}