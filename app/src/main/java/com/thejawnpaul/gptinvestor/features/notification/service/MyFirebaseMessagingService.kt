package com.thejawnpaul.gptinvestor.features.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thejawnpaul.gptinvestor.MainActivity
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyFirebaseMessagingService :
    FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Handle FCM message
        remoteMessage.data.let { data ->
            val deepLinkRoute = data["deep_link"]
            val title = data["title"] ?: "Notification"
            val body = data["body"] ?: "You have a new notification"
            val notificationData = data["notification_data"]

            // Show notification with deep link
            showNotification(title, body, deepLinkRoute, notificationData)
        }

        // Handle notification payload (if sent from Firebase Console)
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notification"
            val body = notification.body ?: "You have a new notification"
            val deepLinkRoute = remoteMessage.data["deep_link"]
            val notificationData = remoteMessage.data["notification_data"]

            showNotification(title, body, deepLinkRoute, notificationData)
        }
    }

    override fun onNewToken(token: String) {
        scope.launch {
            notificationRepository.saveToken(token)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun showNotification(title: String, body: String, deepLinkRoute: String?, notificationData: String?) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Create intent for when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            deepLinkRoute?.let { route ->
                putExtra("deep_link", route)
            }
            notificationData?.let { data ->
                putExtra("notification_data", data)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Show notification
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
