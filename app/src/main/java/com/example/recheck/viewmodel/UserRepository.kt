package com.example.week12.viewmodel

import com.example.week12.roomDB.RecheckDatabase
import com.example.week12.roomDB.UserEntity

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