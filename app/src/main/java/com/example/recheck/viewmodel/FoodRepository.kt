package com.example.recheck.viewmodel

import com.example.week12.roomDB.FoodEntity
import com.example.week12.roomDB.RecheckDatabase

class FoodRepository(private val db: RecheckDatabase) {
    val dao = db.getFoodDao()
    suspend fun insertFood(foodEntity: FoodEntity) {
        dao.insertFood(foodEntity)
    }

    suspend fun consumeFood(foodId: Int) {
        dao.consumeFood(foodId)
    }

    suspend fun deleteFood(foodEntity: FoodEntity) {
        dao.deleteFood(foodEntity)
    }

    suspend fun getMyFoods(userId: Int): List<FoodEntity> {
        return dao.getMyFoods(userId)
    }
}