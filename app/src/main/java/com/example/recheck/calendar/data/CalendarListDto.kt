package com.example.recheck.calendar.data

data class CalendarListResponse(
    val items: List<CalendarListEntryDto>
)

data class CalendarListEntryDto(
    val id: String,       // 실제 캘린더 ID
    val summary: String   // 캘린더 이름
)
