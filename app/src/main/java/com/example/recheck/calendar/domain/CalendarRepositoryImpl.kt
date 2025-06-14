package com.example.recheck.calendar.domain

import com.example.recheck.calendar.data.CalendarApiService
import com.example.recheck.calendar.data.CalendarListEntryDto
import com.example.recheck.roomDB.FoodDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarRepositoryImpl(
    private val foodDao: FoodDAO,
    private val apiService: CalendarApiService
) : CalendarRepository {

    private val dateFormatter = DateTimeFormatter.ISO_DATE

    override suspend fun getAllExpiryItems(userId: Int): List<FoodItem> =
        withContext(Dispatchers.IO) {
            foodDao.getMyFoods(userId)
                .filter { !it.isConsumed }
                .map { it.toDomainModel() }
        }

    override suspend fun getExpiryItemsBetween(
        userId: Int,
        start: LocalDate,
        end: LocalDate
    ): List<FoodItem> = withContext(Dispatchers.IO) {
        foodDao.getMyFoods(userId)
        .map { it.toDomainModel() }
        .filter { item ->
                // start ≤ item.expirationDate ≤ end
                !item.expirationDate.isBefore(start) && !item.expirationDate.isAfter(end)
            }
    }

    override suspend fun fetchRemoteCalendarEvents(
        from: LocalDate,
        to: LocalDate
    ): List<CalendarEvent> = withContext(Dispatchers.IO) {
        // 1) 캘린더 리스트 조회 → 내 계정 ID 선택
        val calList = apiService.listCalendars()
        val primaryId = calList.items.first {
            it.id.contains("@")  // 첫 번째 일반 계정 캘린더
        }.id

        // 2) timeMin/timeMax 계산
        val timeMin = from.atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME) + "Z"
        val timeMax = to.atTime(23,59).format(DateTimeFormatter.ISO_DATE_TIME) + "Z"

        // 3) 실제 이벤트 호출
        val resp = apiService.getEvents(
            calendarId = primaryId,
            timeMin    = timeMin,
            timeMax    = timeMax
        )
        resp.items.map { it.toDomain() }
    }

    override suspend fun getCalendarIds(): List<CalendarListEntryDto> =
        withContext(Dispatchers.IO) {
            apiService.listCalendars()
                .items
        }
}
