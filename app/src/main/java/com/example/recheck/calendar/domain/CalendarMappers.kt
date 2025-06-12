package com.example.recheck.calendar.domain

import com.example.recheck.calendar.data.dto.CalendarEventDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val ISO_DATE_TIME = DateTimeFormatter.ISO_DATE_TIME    // "yyyy-MM-dd'T'HH:mm:ss'Z'"
private val ISO_DATE      = DateTimeFormatter.ISO_DATE         // "yyyy-MM-dd"

/**
 * CalendarEventDto → CalendarEvent(도메인 모델) 변환 확장 함수
 */
fun CalendarEventDto.toDomain(): CalendarEvent {
    // start.dateTime 이 null 이면 all-day 이벤트이므로 date 필드를 사용
    val start = start.dateTime
        ?.let { LocalDateTime.parse(it, ISO_DATE_TIME) }
        ?: run {
            val d = LocalDate.parse(start.date ?: throw IllegalArgumentException("start.date is null"), ISO_DATE)
            d.atStartOfDay()
        }

    // end.dateTime 이 null 이면 date 필드를 사용
    val end = end.dateTime
        ?.let { LocalDateTime.parse(it, ISO_DATE_TIME) }
        ?: run {
            val d = LocalDate.parse(end.date ?: throw IllegalArgumentException("end.date is null"), ISO_DATE)
            d.atStartOfDay()
        }

    return CalendarEvent(
        id    = id,
        title = summary.orEmpty(),
        start = start,
        end   = end
    )
}
