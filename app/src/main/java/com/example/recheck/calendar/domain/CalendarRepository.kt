package com.example.recheck.calendar.domain

import com.example.recheck.calendar.data.CalendarListEntryDto
import java.time.LocalDate

/**
 * 달력 기능에서 “로컬 만료 식재료(FoodItem)”와 “원격 캘린더 일정(CalendarEvent)”을 가져오는 메서드를 정의합니다.
 */
interface CalendarRepository {
    // 로컬 DB에서 해당 사용자의 모든 만료 식재료 가져오기
    suspend fun getAllExpiryItems(userId: Int): List<FoodItem>

    // 로컬 DB에서 해당 사용자의 기간 내 만료 식재료 가져오기
    suspend fun getExpiryItemsBetween(userId: Int, start: LocalDate, end: LocalDate): List<FoodItem>

    // 원격 캘린더 API에서 해당 사용자의 일정 가져오기 (from~to 기간)
    suspend fun fetchRemoteCalendarEvents(from: LocalDate, to: LocalDate): List<CalendarEvent>

    suspend fun getCalendarIds(): List<CalendarListEntryDto>
}