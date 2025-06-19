package com.example.recheck.calendar.domain

import com.example.recheck.roomDB.FoodEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val ISO_DATE = DateTimeFormatter.ISO_DATE  // "yyyy-MM-dd"

fun FoodEntity.toDomainModel(): FoodItem {
    return FoodItem(
        id = this.id,
        name = this.name,
        expirationDate = date,
        userId = this.userId,
        isConsumed = this.isConsumed
    )
}