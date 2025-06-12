package com.example.recheck.calendar.data

import com.example.recheck.calendar.data.dto.CalendarEventsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CalendarApiService {

    // 캘린더 이벤트 조회
    @GET("calendars/{calendarId}/events")
    suspend fun getEvents(
        @Path("calendarId") calendarId: String,       // "primary" 또는 실제 ID
        @Query("timeMin") timeMin: String,
        @Query("timeMax") timeMax: String,
        @Query("singleEvents") singleEvents: Boolean = true,
        @Query("orderBy") orderBy: String = "startTime",
        @Query("maxResults") maxResults: Int = 250,
        @Query("pageToken") pageToken: String? = null,
        @Query("timeZone") timeZone: String = "UTC"
    ): CalendarEventsResponse

    // 캘린더 리스트(아이디)를 가져오는 엔드포인트
    @GET("users/me/calendarList")
    suspend fun listCalendars(): CalendarListResponse
}
