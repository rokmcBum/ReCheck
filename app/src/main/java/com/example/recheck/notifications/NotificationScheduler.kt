package com.example.recheck.notifications

import android.content.Context
import androidx.work.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager

/**
 * WorkManager를 사용해 ExpirationNotificationWorker를 매일 자정에 실행하도록 스케줄링합니다.
 */
object NotificationScheduler {

    private const val WORK_NAME = "expiration_notification_work"

    /**
     * 테스트 용도로 워커를 즉시 실행합니다.
     */
    fun scheduleImmediateCheck(context: Context) {
        val request = OneTimeWorkRequestBuilder<ExpirationNotificationWorker>().build()
        WorkManager.getInstance(context)
            .enqueue(request)
    }

    /**
     * 매일 자정에 ExpirationNotificationWorker를 실행하도록 주기 작업을 등록합니다.
     */
    fun scheduleDailyCheck(context: Context) {
        val workManager = WorkManager.getInstance(context)

        // 이미 스케줄된 작업이 있으면 다시 등록하지 않음
        val existing = workManager.getWorkInfosForUniqueWork(WORK_NAME).get()
        if (existing.isNotEmpty()) return

        // 다음 자정까지 지연 시간 계산
        val now = LocalDateTime.now()
        val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay()
        val initialDelayMillis = Duration.between(now, nextMidnight).toMillis()

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ExpirationNotificationWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyWorkRequest
        )
    }

    /**
     * 등록된 주기 작업을 취소합니다.
     */
    fun cancelDailyCheck(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(WORK_NAME)
    }
}
