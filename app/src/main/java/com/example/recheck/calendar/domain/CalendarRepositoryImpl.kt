package com.example.recheck.calendar.domain

import com.example.recheck.roomDB.FoodDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarRepositoryImpl(
    private val foodDao: FoodDAO,
) : CalendarRepository {

    private val dateFormatter = DateTimeFormatter.ISO_DATE

    override suspend fun getAllExpiryItems(userId: Int): List<FoodItem> =
        withContext(Dispatchers.IO) {
            foodDao.getMyFoods(userId)
                .filter { !it.isConsumed }
                .map { it.toDomainModel() }
        }

    override suspend fun getExpiryItemsBetween(
        userId: Int,
        start: LocalDate,
        end: LocalDate
    ): List<FoodItem> = withContext(Dispatchers.IO) {
        foodDao.getMyFoods(userId)
        .map { it.toDomainModel() }
        .filter { item ->
                // start ≤ item.expirationDate ≤ end
                !item.expirationDate.isBefore(start) && !item.expirationDate.isAfter(end)
            }
    }
}
