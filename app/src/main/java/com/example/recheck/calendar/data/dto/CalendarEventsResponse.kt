package com.example.recheck.calendar.data.dto

data class CalendarEventsResponse(
    val items: List<CalendarEventDto>,
    val nextPageToken: String?,       // 다음 페이지가 있으면 토큰
    val nextSyncToken: String?        // 동기화 토큰 (optional)
)