package com.example.feature.chat.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.feature.chat.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlanNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val channelId = "plan_generation_channel"
    private val shownNotifications = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        try {
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
        } catch (e: Exception) {
            Timber.e(e, "Failed to create notification channel")
        }
    }

    fun showPlanCompletedNotification(
        threadId: String,
        tripId: String
    ) {
        if (!shownNotifications.add(tripId)) {
            return
        }

        try {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
                putExtra("tripId", tripId)
                flags = android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = android.app.PendingIntent.getActivity(
                context,
                tripId.hashCode(),
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(context.getString(R.string.notification_title_completed))
                .setContentText(context.getString(R.string.notification_body_completed))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(tripId.hashCode(), builder.build())
            }
        } catch (e: SecurityException) {
            // Handle missing permission on Android 13+
            Timber.e(e, "Notification permission denied")
        } catch (e: Exception) {
            // Handle platform limitations in unit tests
            Timber.e(e, "Failed to show completed notification: ${e.message}")
        }
    }

    fun showPlanFailedNotification(threadId: String) {
        if (!shownNotifications.add(threadId)) {
            return
        }

        try {
            val builder = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(context.getString(R.string.notification_title_failed))
                .setContentText(context.getString(R.string.notification_body_failed, threadId))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                notify(threadId.hashCode(), builder.build())
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Notification permission denied")
        } catch (e: Exception) {
            // Handle platform limitations in unit tests
            Timber.e(e, "Failed to show failed notification: ${e.message}")
        }
    }
}
