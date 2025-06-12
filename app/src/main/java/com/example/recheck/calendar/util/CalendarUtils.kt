package com.example.recheck.calendar.util

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * 달력 표시나 만료일 계산에 필요한 유틸 함수 모음
 */
object CalendarUtils {
    /** 오늘 기준으로 expiryDate까지 며칠 남았는지 */
    fun daysUntilExpiry(expiryDate: LocalDate): Long {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate)
    }

    /** 주어진 연/월에 속한 날짜 리스트 생성 (1일~마지막 날까지) */
    fun generateMonthDates(year: Int, month: Int): List<LocalDate> {
        val firstDay = LocalDate.of(year, month, 1)
        val lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth())
        val daysCount = ChronoUnit.DAYS.between(firstDay, lastDay) + 1
        return (0 until daysCount).map { offset -> firstDay.plusDays(offset) }
    }

    /** 이번 달의 첫째 날 */
    fun getFirstDayOfMonth(date: LocalDate): LocalDate {
        return date.withDayOfMonth(1)
    }

    /** 이번 달의 마지막 날 */
    fun getLastDayOfMonth(date: LocalDate): LocalDate {
        return date.withDayOfMonth(date.lengthOfMonth())
    }
}