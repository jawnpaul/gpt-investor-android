package com.thejawnpaul.gptinvestor.features.notification.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.core.app.NotificationCompat
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
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

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val deepLinkRoute = data["deep_link"]
        val notificationData = data["notification_data"]
        val notification = remoteMessage.notification

        // Messages with a notification block also carry data; post them once, not once per part.
        if (notification != null) {
            showNotification(
                title = notification.title ?: "Notification",
                body = notification.body ?: "You have a new notification",
                deepLinkRoute = deepLinkRoute,
                notificationData = notificationData,
                imageUrl = notification.imageUrl?.toString()
            )
        } else {
            showNotification(
                title = data["title"] ?: "Notification",
                body = data["body"] ?: "You have a new notification",
                deepLinkRoute = deepLinkRoute,
                notificationData = notificationData,
                imageUrl = data["image_url"]
            )
        }
    }

    override fun onNewToken(token: String) {
        scope.launch {
            notificationRepository.saveToken(token)
            notificationRepository.syncTokenIfNeeded()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun showNotification(
        title: String,
        body: String,
        deepLinkRoute: String?,
        notificationData: String?,
        imageUrl: String?
    ) {
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

        // Distinct per deep link so alerts for different tickers don't replace each other or share a tap target.
        val notificationId = deepLinkRoute?.hashCode() ?: NOTIFICATION_ID

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
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
            notificationManager.notify(notificationId, notificationBuilder.build())
        } else {
            // Load image with injected Coil ImageLoader
            scope.launch {
                loadImageAndShowNotification(
                    imageUrl,
                    notificationBuilder,
                    notificationManager,
                    notificationId
                )
            }
        }
    }

    private suspend fun loadImageAndShowNotification(
        imageUrl: String,
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManager,
        notificationId: Int
    ) {
        try {
            val request = ImageRequest.Builder(this)
                .data(imageUrl)
                .allowHardware(false) // Disable hardware bitmaps for notifications
                .build()

            when (val result = ImageLoader(this).execute(request)) {
                is SuccessResult -> {
                    val bitmap = (result.image.asDrawable(resources) as? BitmapDrawable)?.bitmap

                    bitmap?.let { picture ->
                        val notification = notificationBuilder
                            .setLargeIcon(picture)
                            .setStyle(
                                NotificationCompat.BigPictureStyle()
                                    .bigPicture(picture)
                                    .bigLargeIcon(null as Bitmap?)
                            )
                            .build()

                        notificationManager.notify(notificationId, notification)
                    } ?: showFallbackNotification(notificationBuilder, notificationManager, notificationId)
                }

                else -> showFallbackNotification(notificationBuilder, notificationManager, notificationId)
            }
        } catch (_: Exception) {
            showFallbackNotification(notificationBuilder, notificationManager, notificationId)
        }
    }

    private fun showFallbackNotification(
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManager,
        notificationId: Int
    ) {
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val CHANNEL_ID = "fcm_default_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
