package com.example.feature.chat.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val channelId = "plan_generation_channel"
    private val notificationId = 1001

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Plan Generation"
            val descriptionText = "Notifications for travel plan generation status"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPlanCompletedNotification(threadId: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Plan Ready!")
            .setContentText("Your travel plan for thread $threadId is ready.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            // Handle missing permission on Android 13+
            e.printStackTrace()
        }
    }

    fun showPlanFailedNotification(threadId: String) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Plan Generation Failed")
            .setContentText("Failed to generate plan for thread $threadId.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
