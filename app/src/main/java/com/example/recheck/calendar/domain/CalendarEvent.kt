package com.example.recheck.calendar.domain

import java.time.LocalDateTime

/**
 * 캘린더 API에서 가져온 일정 정보를 담는 도메인 모델
 */
data class CalendarEvent(
    val id: String,
    val title: String,
    val start: LocalDateTime,
    val end: LocalDateTime
)