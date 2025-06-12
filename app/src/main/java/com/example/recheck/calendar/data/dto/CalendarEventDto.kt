package com.example.recheck.calendar.data.dto

data class CalendarEventDto(
    val id: String,
    val summary: String?,
    val description: String?,
//    val location: String?,
    val start: EventDateTimeDto,
    val end: EventDateTimeDto,
)
