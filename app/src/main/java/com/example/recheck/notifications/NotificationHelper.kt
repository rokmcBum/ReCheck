package com.example.recheck.notifications

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.recheck.R

object NotificationHelper {
    // Notification Channel ID
    const val CHANNEL_ID = "expiration_channel"
    private const val CHANNEL_NAME = "식재료 만료 알림"
    private const val CHANNEL_DESCRIPTION = "2일 전·1일 전·당일 만료 식재료 알림을 제공합니다"

    /**
     * 앱 시작 시, Notification Channel을 생성합니다.
     * Android 8.0(O) 이상에서만 필요합니다.
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * 주어진 ID로 알림을 표시합니다.
     * @param id 알림 식별자 (각 식재료마다 고유하게 부여)
     * @param title 알림 제목
     * @param message 알림 내용
     */

    @SuppressLint("MissingPermission")
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        context: Context, id: Int, title: String, message: String
    ) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context)
            .notify(id, builder.build())
    }

    fun openNotificationSettings(context: Context) {
        // 앱 전체 알림 설정으로 이동
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            // flags 필요
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
