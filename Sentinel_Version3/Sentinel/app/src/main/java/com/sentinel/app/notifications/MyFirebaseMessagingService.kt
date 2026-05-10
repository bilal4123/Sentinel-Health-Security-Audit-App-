package com.sentinel.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sentinel.app.R
import com.sentinel.app.activities.MainActivity
import com.google.firebase.firestore.FirebaseFirestore

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "sentinel_security_channel"
        const val CHANNEL_NAME = "Security Alerts"
        const val NOTIFICATION_ID = 1001
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Security Alert"
        val body = remoteMessage.notification?.body ?: "New threat detected!"
        val riskLevel = remoteMessage.data["risk_level"] ?: "MEDIUM"

        sendNotification(title, body, riskLevel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveTokenToFirestore(token)
    }

    private fun sendNotification(title: String, message: String, riskLevel: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // FIXED: Use Int color (not Long)
        val color = when (riskLevel.lowercase()) {
            "high" -> 0xFF5252  // Int, not Long
            "medium" -> 0xFFA726
            else -> 0xFF4CAF50
        }.toInt()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_shield)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(color)  // Now Int, matches expected type

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Security alerts and threat notifications"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun saveTokenToFirestore(token: String) {
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val tokenData = hashMapOf(
                "token" to token,
                "userId" to userId,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("fcm_tokens").document(userId)
                .set(tokenData)
                .addOnSuccessListener {
                    // Token saved
                }
                .addOnFailureListener {
                    // Handle error
                }
        }
    }
}