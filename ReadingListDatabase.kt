package com.example.myapplication.data.readingList

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.utils.ReadingListBook

@Database(entities = [ReadingListBook::class], version = 2, exportSchema = false)
abstract class ReadingListDatabase: RoomDatabase() {
    abstract fun readingListDao(): ReadingListDao

    companion object {
        @Volatile
        private var INSTANCE: ReadingListDatabase? = null

        fun getDatabase(context: Context): ReadingListDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReadingListDatabase::class.java,
                    "MyReadingList"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}