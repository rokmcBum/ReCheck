package com.example.recheck.calendar.domain

import com.example.recheck.roomDB.FoodEntity

fun FoodEntity.toDomainModel(): FoodItem {
    return FoodItem(
        id             = this.id,
        name           = this.name,
        expirationDate = this.expirationDate,
        userId         = this.userId,
        isConsumed     = this.isConsumed
    )
}