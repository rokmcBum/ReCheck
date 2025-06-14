package com.example.recheck.calendar.domain

import java.time.LocalDate

/**
 * 도메인 레이어(앱 비즈니스 로직 & UI)에서 사용하는 순수 모델
 * Room Entity(FoodEntity)와 달리, expirationDate를 LocalDate 타입으로 가집니다.
 */
data class FoodItem(
    val id: Int,
    val name: String,
    val expirationDate: LocalDate,
    val userId: Int,
    val isConsumed: Boolean
)
