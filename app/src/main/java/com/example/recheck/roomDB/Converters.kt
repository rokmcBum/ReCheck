package com.example.recheck.roomDB

import androidx.room.TypeConverter
import java.time.LocalDate

object Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? =
        value?.let { LocalDate.parse(it) }
}
