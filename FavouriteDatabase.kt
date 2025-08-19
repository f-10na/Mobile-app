package com.example.myapplication.data.favourites

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.utils.RoomBook

@Database(entities = [RoomBook::class], version = 2, exportSchema = false)
abstract class FavouriteDatabase: RoomDatabase() {
    abstract fun favouriteDao(): FavouriteDao
    companion object {
        @Volatile
        private var INSTANCE: FavouriteDatabase? = null

        fun getDatabase(context: Context): FavouriteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavouriteDatabase::class.java,
                    "MyFavourites"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}