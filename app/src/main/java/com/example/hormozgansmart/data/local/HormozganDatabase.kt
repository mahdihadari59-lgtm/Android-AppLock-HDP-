package com.example.hormozgansmart.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        CustomNoteEntity::class,
        SearchHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HormozganDatabase : RoomDatabase() {
    abstract fun dao(): HormozganDao

    companion object {
        @Volatile
        private var INSTANCE: HormozganDatabase? = null

        fun getDatabase(context: Context): HormozganDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HormozganDatabase::class.java,
                    "hormozgan_smart_master.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
