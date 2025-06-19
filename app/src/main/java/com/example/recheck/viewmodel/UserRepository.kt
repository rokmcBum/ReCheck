package com.example.recheck.viewmodel

import com.example.recheck.roomDB.FoodEntity
import com.example.recheck.roomDB.RecheckDatabase
import com.example.recheck.roomDB.UserEntity

class UserRepository(private val db: RecheckDatabase) {
    val dao = db.getUserDao()

    suspend fun insertUser(userEntity: UserEntity) {
        dao.insertUser(userEntity)
    }

    suspend fun updateUser(userEntity: UserEntity) {
        dao.updateUser(userEntity)
    }

    suspend fun deleteUser(userEntity: UserEntity) {
        dao.deleteUser(userEntity)
    }

    suspend fun getUser(email: String, password: String): UserEntity? {
        return dao.getUser(email, password)
    }


}