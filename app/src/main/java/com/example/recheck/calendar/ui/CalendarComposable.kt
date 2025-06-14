package com.example.recheck.calendar.ui

import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recheck.R
import com.example.recheck.calendar.domain.CalendarEvent
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.math.LinearTransformation.vertical
import java.time.DayOfWeek
import java.time.LocalDate

@Composable
fun CalendarComposable(
    currentMonth: LocalDate,
    onMonthChanged: (LocalDate) -> Unit,
    markedDates: List<LocalDate>,
    events: List<CalendarEvent>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    mainColor: Color = Color(0xFFFF5D5D)
) {

    // ① 네비게이션 헤더
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
                ) {
        IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(
                    painter           = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                    contentDescription = "이전 달",
                    tint               = Color(0xFF656565)
                )
            }
        Text(
                text      = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                style     = MaterialTheme.typography.titleMedium,
                modifier  = Modifier.weight(1f),
                textAlign = TextAlign.Center
                    )
        IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(
                    painter           = painterResource(id = R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "이전 달",
                    tint               = Color(0xFF656565)
                )
            }
        }

    // ② 날짜 계산
    val firstOfMonth = currentMonth
    val daysInMonth  = currentMonth.lengthOfMonth()

    // 1) 요일 헤더
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("일","월","화","수","목","금","토").forEachIndexed { idx, dow ->
            val color = when (idx) {
                0    -> Color(0xFFFF5D5D)
                6    -> Color(0xFF5D9EFF)
                else -> Color.Gray.copy(alpha = 0.8f)
            }
            Text(
                text      = dow,
                color     = color,
                style     = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                modifier  = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }

    // 2) 날짜 그리드
    var dayCounter = 1
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(6) { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { dow ->
                    if (week == 0 && dow < (firstOfMonth.dayOfWeek.value % 7)) {
                        Spacer(modifier = Modifier.weight(1f).height(48.dp))
                    } else if (dayCounter <= daysInMonth) {
                        val date = firstOfMonth.withDayOfMonth(dayCounter)
                        // 요일별 색 분기
                        val textColor = when (date.dayOfWeek) {
                            DayOfWeek.SUNDAY   -> Color(0xFFFF5D5D)
                            DayOfWeek.SATURDAY -> Color(0xFF5D9EFF)
                            else               -> Color.Black
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clickable { onDateSelected(date) },
                            contentAlignment = Alignment.Center
                        ) {
                            // 선택된 날짜 배경
                            if (date == selectedDate) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(mainColor.copy(alpha = 0.2f))
                                )
                            }

                            // 날짜 숫자
                            Text(
                                text  = dayCounter.toString(),
                                color = textColor,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )

                            // 만료일 점
                            if (markedDates.contains(date)) {
                                Box(
                                    modifier = Modifier
                                        .offset(y = 12.dp)
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(mainColor)
                                )
                            }
                            // 원격 일정 점
                            if (events.any { it.start.toLocalDate() == date }) {
                                Box(
                                    modifier = Modifier
                                        .offset(y = 20.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(mainColor)
                                )
                            }
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f).height(48.dp))
                    }
                }
            }
        }
    }
}
