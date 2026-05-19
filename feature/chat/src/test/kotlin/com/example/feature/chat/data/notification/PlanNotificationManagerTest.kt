package com.example.feature.chat.data.notification

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import com.example.feature.chat.R
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PlanNotificationManagerTest {

    private lateinit var context: Context
    private lateinit var notificationManager: PlanNotificationManager

    @Before
    fun setUp() {
        context = mock()
        val mockNotificationManager = mock<NotificationManager>()
        whenever(context.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
        
        whenever(context.getString(R.string.notification_title_completed)).thenReturn("AI Trip Plan Ready")
        whenever(context.getString(R.string.notification_body_completed)).thenReturn("Click to open")
        whenever(context.getString(R.string.notification_title_failed)).thenReturn("Plan Failed")
        whenever(context.getString(R.string.notification_body_failed, "thread_123")).thenReturn("Failed for thread")

        whenever(context.packageName).thenReturn("com.example.travio")

        val packageManager = mock<PackageManager>()
        whenever(context.packageManager).thenReturn(packageManager)

        notificationManager = PlanNotificationManager(context)
    }

    @Test
    fun should_prevent_duplicate_completed_notifications_for_same_trip() {
        val threadId = "thread_123"
        val tripId = "trip_456"

        notificationManager.showPlanCompletedNotification(threadId, tripId)
        notificationManager.showPlanCompletedNotification(threadId, tripId)

        verify(context, org.mockito.kotlin.times(1)).getString(R.string.notification_title_completed)
    }

    @Test
    fun should_prevent_duplicate_failed_notifications_for_same_thread() {
        val threadId = "thread_123"

        notificationManager.showPlanFailedNotification(threadId)
        notificationManager.showPlanFailedNotification(threadId)

        verify(context, org.mockito.kotlin.times(1)).getString(R.string.notification_title_failed)
    }
}
