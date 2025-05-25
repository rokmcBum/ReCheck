package com.example.week12.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class],
    version = 2,
    exportSchema = true
)
abstract class RecheckDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDAO

    companion object {
        @Volatile
        private var DBInstance: RecheckDatabase? = null
        fun getDBInstance(context: Context): RecheckDatabase {
            return DBInstance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecheckDatabase::class.java,
                    "recheckDB"
                ).fallbackToDestructiveMigration(false).build()
                DBInstance = instance
                instance
            }
        }
    }
}