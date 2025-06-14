package com.example.recheck.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.week12.roomDB.RecheckDatabase
import java.time.LocalDate

/**
 * 매일 자정에 실행되어, 소비기한이 2일 전·1일 전·당일인 식재료에 대해 알림을 생성하는 Worker
 */
class ExpirationNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Notification POST 권한 검사 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                // 권한이 없으면 알림 없이 정상 종료
                return Result.success()
            }
        }

        val dao = RecheckDatabase.getDBInstance(applicationContext).getFoodDao()
        val today = LocalDate.now()
        val offsets = listOf(2L, 1L, 0L)

        offsets.forEach { offset ->
            val targetDate = today.plusDays(offset)
            val items = dao.findItemsByExpirationDate(targetDate)

            items.forEach { item ->
                val dayLabel = when (offset) {
                    2L -> "2일 전"
                    1L -> "1일 전"
                    0L -> "오늘"
                    else -> ""
                }
                val title = "식재료 만료 알림"
                val message = "${item.name}의 소비기한이 $dayLabel 입니다."

                val notificationId = item.id.hashCode() + offset.toInt()

                try {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = notificationId,
                        title = title,
                        message = message
                    )
                } catch (e: SecurityException) {
                    // 권한 문제 발생 시 안전하게 무시
                }
            }
        }

        return Result.success()
    }
}
