package com.sentinel.app.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sentinel.app.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "sentinel_security_channel"
        const val CHANNEL_NAME = "Security Alerts"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Security alerts and threat notifications"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // FIXED: Use NotificationManagerCompat correctly
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showHighRiskAlert(appName: String, riskScore: Int) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle("⚠️ High Risk App Detected")
            .setContentText("$appName has a risk score of $riskScore")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(
            MyFirebaseMessagingService.NOTIFICATION_ID + 1,
            builder.build()
        )
    }
}