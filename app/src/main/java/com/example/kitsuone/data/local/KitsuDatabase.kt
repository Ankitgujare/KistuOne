package com.example.kitsuone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kitsuone.data.local.dao.WatchlistDao
import com.example.kitsuone.data.local.entity.WatchlistEntity

@Database(
    entities = [WatchlistEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KitsuDatabase : RoomDatabase() {
    
    abstract fun watchlistDao(): WatchlistDao
    
    companion object {
        @Volatile
        private var INSTANCE: KitsuDatabase? = null
        
        fun getDatabase(context: Context): KitsuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KitsuDatabase::class.java,
                    "kitsu_database"
                )
                    .fallbackToDestructiveMigration() // For development, remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
