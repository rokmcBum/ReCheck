package com.example.recheck.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "FoodTable")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    var name: String,
    var expirationDate: LocalDate,
    var userId: Int,
    var isConsumed: Boolean,
)