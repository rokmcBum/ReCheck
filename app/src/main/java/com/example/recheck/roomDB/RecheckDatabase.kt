package com.example.recheck.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.recheck.roomDB.Converters
import com.example.recheck.roomDB.FoodDAO
import com.example.recheck.roomDB.FoodEntity

@Database(
    entities = [UserEntity::class, FoodEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class RecheckDatabase : RoomDatabase() {
    abstract fun getUserDao(): UserDAO
    abstract fun getFoodDao(): FoodDAO

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