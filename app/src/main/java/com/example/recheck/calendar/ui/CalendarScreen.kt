package com.example.recheck.calendar.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.recheck.R
import com.example.recheck.calendar.domain.CalendarRepositoryImpl
import com.example.recheck.calendar.domain.FoodItem
import com.example.recheck.calendar.viewmodel.CalendarViewModel
import com.example.recheck.calendar.viewmodel.CalendarViewModelFactory
import com.example.recheck.model.Routes
import com.example.recheck.notifications.NotificationScheduler
import com.example.recheck.roomDB.FoodDAO
import com.example.recheck.roomDB.RecheckDatabase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    currentUserId: Int,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext

    // 1) 싱글톤 DB 인스턴스에서 DAO 가져오기
    val db: RecheckDatabase = RecheckDatabase.getDBInstance(context)
    val foodDao: FoodDAO = remember { db.getFoodDao() }

    // 2) Repository + ViewModel 초기화
    val repository = remember { CalendarRepositoryImpl(foodDao) }
    val calendarViewModel: CalendarViewModel = viewModel(
        factory = CalendarViewModelFactory(repository, currentUserId)
    )

    // ── ① 현재 보고 있는 월(1일 기준) 상태
    var currentMonth by rememberSaveable { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }

    // ② 달이 바뀔 때 실행할 콜백 (로컬 데이터만 사용)
    val onMonthChanged: (LocalDate) -> Unit = { newMonth ->
        currentMonth = newMonth
    }

    val uiState by calendarViewModel.uiState.collectAsState()

    // 6) 날짜 포맷터 준비
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    Box(modifier = modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            return@Box
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFFFFFFF))
                    .padding(16.dp)
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "캘린더",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall
                )
                IconButton(onClick = { navController.navigate(Routes.Notification.route) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_notifications_none_24),
                        contentDescription = "알림 설정",
                        tint = Color(0xFF656565)
                    )
                }
            }

//            // 푸시알림 테스트
//            Button(onClick = { NotificationScheduler.scheduleImmediateCheck(context) }) {
//                Text("만료 알림 테스트")
//            }

            Column (modifier = Modifier.background(color = Color(0xFFFFFFFF))){
                // A) 달력: 로컬 만료일 점 찍기
                CalendarComposable(
                    currentMonth = currentMonth,
                    onMonthChanged = onMonthChanged,
                    markedDates = uiState.markedDates,
                    selectedDate = uiState.selectedDate,
                    onDateSelected = calendarViewModel::onDateSelected,
                    mainColor = Color(0xFFFF5D5D)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(0xFFFFFFFF))
                        .border(1.dp, Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                        .shadow(
                            elevation = 8.dp,
                            spotColor = Color(0x08000000),
                            ambientColor = Color(0x08000000)
                        )
                ) {
                    Column (modifier = Modifier.padding(16.dp)){
                        // B) 선택된 날짜
                        val sel = uiState.selectedDate
                        Text(
                            text = sel.format(formatter),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFA1A1A1),
                            modifier = Modifier.padding(16.dp)
                        )

                        // C) 만료 식재료 리스트
                        val foods: List<FoodItem> = uiState.itemsByDate[sel].orEmpty()
                        if (foods.isEmpty()) {
                            Text("소비기한이 끝나는 식재료가 없어요.", modifier = Modifier.padding(16.dp))
                        } else {
                            foods.forEach { item ->
                                Text(
                                    text = "- ${item.name}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))


            }
        }
        // E) 에러 메시지
        uiState.errorMessage?.let { err ->
            Text(
                text = err,
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}
