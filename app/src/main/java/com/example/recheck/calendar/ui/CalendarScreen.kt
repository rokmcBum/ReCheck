package com.example.recheck.calendar.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recheck.calendar.data.CalendarApiClient
import com.example.recheck.calendar.domain.CalendarRepositoryImpl
import com.example.recheck.calendar.domain.FoodItem
import com.example.recheck.calendar.viewmodel.CalendarViewModel
import com.example.recheck.calendar.viewmodel.CalendarViewModelFactory
import com.example.recheck.roomDB.FoodDAO
import com.example.week12.roomDB.RecheckDatabase
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.getValue

@Composable
fun CalendarScreen(
    currentUserId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext

    // 0) SharedPreferences 에 저장된 토큰 꺼내기
    val prefs = context.getSharedPreferences("ReCheckPrefs", Context.MODE_PRIVATE)
    val token = prefs.getString("calendar_token", "") ?: ""

    // 1) 싱글톤 DB 인스턴스에서 DAO 가져오기
    val db: RecheckDatabase = RecheckDatabase.getDBInstance(context)
    val foodDao: FoodDAO    = remember { db.getFoodDao() }

    // 2) Retrofit CalendarApiService 생성 (Interceptor 로 토큰 주입)
    val apiService = remember(token) { CalendarApiClient.create(token) }

    // 3) Repository + ViewModel 초기화
    val repository = remember(token) { CalendarRepositoryImpl(foodDao, apiService) }
    val vm: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(repository, currentUserId)
    )

    val calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(repository, currentUserId)
    )

    val uiState by vm.uiState.collectAsState()

    // 6) 날짜 포맷터 준비
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "캘린더",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // A) 달력: 로컬 만료일 + 원격 일정 점 찍기
            CalendarComposable(
                markedDates   = uiState.markedDates,
                events         = uiState.remoteEvents,
                onDateSelected = { date -> vm.onDateSelected(date) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // B) 선택된 날짜
            val sel = uiState.selectedDate
            Text(
                text = sel.format(formatter),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // C) 만료 식재료 리스트
            val foods: List<FoodItem> = uiState.itemsByDate[sel].orEmpty()
            if (foods.isEmpty()) {
                Text("- 만료 식재료 없음", modifier = Modifier.padding(start = 8.dp))
            } else {
                foods.forEach { item ->
                    Text(
                        text     = "- ${item.name}",
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // D) 원격 일정 리스트
            val evts = uiState.remoteEvents.filter { it.start.toLocalDate() == sel }
            if (evts.isEmpty()) {
                Text("- 일정 없음", modifier = Modifier.padding(start = 8.dp))
            } else {
                evts.forEach { ev ->
                    val t0 = ev.start.toLocalTime().toString().take(5)
                    val t1 = ev.end  .toLocalTime().toString().take(5)
                    Text(
                        text     = "- ${ev.title} ($t0~$t1)",
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }
            }
        }

        // E) 에러 메시지
        uiState.errorMessage?.let { err ->
            Text(
                text     = err,
                color    = androidx.compose.ui.graphics.Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
