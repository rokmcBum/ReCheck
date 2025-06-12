package com.example.recheck.calendar.viewmodel

import com.example.recheck.calendar.domain.FoodItem
import com.example.recheck.calendar.domain.CalendarEvent
import java.time.LocalDate

data class CalendarScreenState(
    val markedDates: List<LocalDate> = emptyList(),                // 로컬 만료일
    val selectedDate: LocalDate = LocalDate.now(),                  // 선택 중인 날짜
    val itemsByDate: Map<LocalDate, List<FoodItem>> = emptyMap(),   // 날짜별 식재료
    val remoteEvents: List<CalendarEvent> = emptyList(),            // 원격 일정
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
