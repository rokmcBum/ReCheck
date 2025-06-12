package com.example.recheck.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recheck.calendar.domain.FoodItem
import com.example.recheck.calendar.domain.CalendarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.map

class CalendarViewModel(
    private val repository: CalendarRepository,
    private val userId: Int
) : ViewModel() {

    private val _uiState: MutableStateFlow<CalendarScreenState> =
        MutableStateFlow(CalendarScreenState(isLoading = true))
    val uiState: StateFlow<CalendarScreenState> = _uiState

    init {
        // 뷰모델이 만들어지는 순간 한 번만 실행
        loadAllData()
    }

    fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // ① 로컬 만료 식재료 가져오기
            val items: List<FoodItem> = repository.getAllExpiryItems(userId)

            // ② expirationDate 만 뽑아서 distinct
            val dates: List<LocalDate> = items
                .map { it.expirationDate }
                .distinct()

            // ③ 날짜별 아이템 그룹화
            val byDate: Map<LocalDate, List<FoodItem>> =
                items.groupBy { it.expirationDate }

            // ④ 원격 이벤트도 같이 가져오기
            val today     = LocalDate.now()
            val weekLater = today.plusDays(7)
            val events    = repository.fetchRemoteCalendarEvents(today, weekLater)

            // ⑤ 상태 업데이트
            _uiState.value = CalendarScreenState(
                markedDates   = dates,
                selectedDate  = today,
                itemsByDate   = byDate,
                remoteEvents  = events,
                isLoading     = false,
                errorMessage  = null
            )
        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }
}