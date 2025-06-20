package com.example.recheck.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.recheck.roomDB.RecheckDatabase
import java.time.LocalDate

class ExpirationNotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // Android 13 이상: POST_NOTIFICATIONS 권한이 없으면 바로 종료
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.success()
            }
        }

        val dao = RecheckDatabase.getDBInstance(applicationContext).getFoodDao()
        val today = LocalDate.now()

        // 2일 전, 1일 전, 당일
        listOf(2L to "2일 전", 1L to "1일 전", 0L to "오늘")
            .forEach { (offsetDays, label) ->
                val targetDate = today.plusDays(offsetDays)
                val items = dao.findItemsByExpirationDate(targetDate)

                items.forEach { food ->
                    val notificationId = (food.id * 10 + offsetDays).toInt()
                    val title = "식재료 만료 알림"
                    val message = "\"${food.name}\"\n소비기한이 $label 입니다."

                    try {
                        NotificationHelper.showNotification(
                            context = applicationContext,
                            id      = notificationId,
                            title   = title,
                            message = message
                        )
                    } catch (_: SecurityException) {
                        // 권한이 없거나 실패해도 워커는 성공으로 종료
                    }
                }
            }

        return Result.success()
    }
}
