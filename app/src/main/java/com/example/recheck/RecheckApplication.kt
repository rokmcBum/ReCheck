package com.example.recheck

import android.app.Application
import com.example.recheck.notifications.NotificationHelper
import com.example.recheck.notifications.NotificationScheduler

class RecheckApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // 1) 알림 채널 생성 (안드로이드 8.0 이상에서 필수)
        NotificationHelper.createNotificationChannel(this)

        // 2) 매일 자정(또는 원하는 시간)에 만료 체크 워커 등록
        NotificationScheduler.scheduleDailyCheck(this)
    }
}