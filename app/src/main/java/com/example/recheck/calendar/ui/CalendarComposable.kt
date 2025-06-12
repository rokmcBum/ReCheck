package com.example.recheck.calendar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recheck.calendar.domain.CalendarEvent
import java.time.LocalDate

/**
 * 아주 간단한 “달력 형태” Composable 예시
 *
 * - markedDates: “만료 식재료”가 있는 날짜 목록 (빨간 점)
 * - events: “원격 일정”이 있는 날짜 목록 (파란 점)
 * - onDateSelected: 날짜 선택 콜백
 *
 * 실제로는 Grid(7열 × 5~6행) 형태로 날자를 그린 뒤,
 * 날짜별로 색인하여 점을 찍어 주시는 로직을 넣어야 합니다.
 */
@Composable
fun CalendarComposable(
    markedDates: List<LocalDate>,
    events: List<CalendarEvent>,
    onDateSelected: (LocalDate) -> Unit
) {
    // 예시: 이번 달 1일을 기준으로 간단히 “7열 × 5행” 구조를 만들었다고 가정합니다.
    val today = LocalDate.now()
    val firstOfMonth = today.withDayOfMonth(1)
    val daysInMonth = today.lengthOfMonth()

    // (간단한 월 표시 예시) 날짜를 1부터 daysInMonth까지 순서대로 나열
    Column(modifier = Modifier.fillMaxWidth()) {
        // 요일 헤더 (일,월,...토)
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("일", "월", "화", "수", "목", "금", "토").forEach { dow ->
                Text(
                    text = dow,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    color = Color.DarkGray
                )
            }
        }
        // 날자 셀 (간단하게 숫자를 나열)
        var currentDay = 1
        repeat(6) { weekIndex ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dowIndex ->
                    if (weekIndex == 0 && dowIndex < firstOfMonth.dayOfWeek.value % 7) {
                        // 이번 달 첫째 날이 오기 전 빈 칸
                        Box(modifier = Modifier.weight(1f).height(48.dp)) { }
                    } else if (currentDay <= daysInMonth) {
                        val thisDate = firstOfMonth.withDayOfMonth(currentDay)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable { onDateSelected(thisDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            // 날짜 숫자
                            Text(text = currentDay.toString())

                            // 만료 식재료가 있는 날에는 빨간 점
                            if (markedDates.contains(thisDate)) {
                                Box(
                                    modifier = Modifier
                                        .offset(y = 10.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                            }
                            // 원격 일정이 있는 날에는 파란 점
                            if (events.any { it.start.toLocalDate() == thisDate }) {
                                Box(
                                    modifier = Modifier
                                        .offset(y = 18.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Blue)
                                )
                            }
                        }
                        currentDay++
                    } else {
                        // 달이 끝난 후 빈 칸
                        Box(modifier = Modifier.weight(1f).height(48.dp)) { }
                    }
                }
            }
        }
    }
}