package com.primetech.safechat.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


@androidx.room.Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true
)
abstract class Database: RoomDatabase() {
        abstract fun userDao(): UserDao

        companion object{
            @Volatile
            private var INSTANCE: Database? = null

            fun getDatabase(context: Context): Database {
                val tempInstance = INSTANCE

                if (tempInstance != null) {
                    return tempInstance
                }
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java,
                        "user_database"
                    ).allowMainThreadQueries().build()
                    INSTANCE = instance
                    return instance
                }
            }
        }

}