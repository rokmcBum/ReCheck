package com.example.recheck.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.LocalDate


@Dao
interface FoodDAO {
    @Insert
    suspend fun insertFood(foodEntity: FoodEntity)

    //@Query("UPDATE FoodTable SET isConsumed = NOT isConsumed WHERE id = :id")
    //suspend fun consumeFood(id: Int)

    @Delete
    suspend fun deleteFood(foodEntity: FoodEntity)

    @Query("SELECT * FROM FoodTable WHERE userId = :userId  AND length(expirationDate) = 10")
    suspend fun getMyFoods(userId: Int): List<FoodEntity>

    @Query("SELECT * FROM FoodTable WHERE expirationDate = :date")
    suspend fun findItemsByExpirationDate(date: LocalDate): List<FoodEntity>

    @Query("""
        UPDATE FoodTable
        SET isConsumed = NOT isConsumed , consumeCount = consumeCount + 1
        WHERE id = :id
    """)
    suspend fun consumeFood(id: Int)

    @Query("SELECT * FROM FoodTable WHERE userId = :userId")
    suspend fun getFoodsByUserId(userId: Int): List<FoodEntity>

}