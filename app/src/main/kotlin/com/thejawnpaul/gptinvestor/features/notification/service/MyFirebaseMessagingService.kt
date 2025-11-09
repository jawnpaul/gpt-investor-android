package com.thejawnpaul.gptinvestor.features.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.thejawnpaul.gptinvestor.MainActivity
import com.thejawnpaul.gptinvestor.R
import com.thejawnpaul.gptinvestor.features.notification.domain.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationRepository: NotificationRepository by inject()

    private val imageLoader: ImageLoader by inject()

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
            val imageUrl = data["image_url"]

            showNotification(title, body, deepLinkRoute, notificationData, imageUrl)
        }

        // Handle notification payload (if sent from Firebase Console)
        remoteMessage.notification?.let { notification ->
            val title = notification.title ?: "Notification"
            val body = notification.body ?: "You have a new notification"
            val deepLinkRoute = remoteMessage.data["deep_link"]
            val notificationData = remoteMessage.data["notification_data"]
            val imageUrl = notification.imageUrl?.toString()

            showNotification(title, body, deepLinkRoute, notificationData, imageUrl)
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

    private fun showNotification(title: String, body: String, deepLinkRoute: String?, notificationData: String?, imageUrl: String?) {
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

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_stat_ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (imageUrl.isNullOrEmpty()) {
            // Show notification without image
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        } else {
            // Load image with injected Coil ImageLoader
            scope.launch {
                loadImageAndShowNotification(
                    imageUrl,
                    notificationBuilder,
                    notificationManager
                )
            }
        }
    }

    private suspend fun loadImageAndShowNotification(imageUrl: String, notificationBuilder: NotificationCompat.Builder, notificationManager: NotificationManager) {
        try {
            val request = ImageRequest.Builder(this)
                .data(imageUrl)
                .allowHardware(false) // Disable hardware bitmaps for notifications
                .build()

            when (val result = imageLoader.execute(request)) {
                is SuccessResult -> {
                    val bitmap = (result.drawable as? BitmapDrawable)?.bitmap

                    bitmap?.let { picture ->
                        val notification = notificationBuilder
                            .setLargeIcon(picture)
                            .setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(picture)
                                    .bigLargeIcon(null as Bitmap?)
                            )
                            .build()

                        notificationManager.notify(NOTIFICATION_ID, notification)
                    } ?: showFallbackNotification(notificationBuilder, notificationManager)
                }

                else -> showFallbackNotification(notificationBuilder, notificationManager)
            }
        } catch (e: Exception) {
            showFallbackNotification(notificationBuilder, notificationManager)
        }
    }

    private fun showFallbackNotification(notificationBuilder: NotificationCompat.Builder, notificationManager: NotificationManager) {
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
