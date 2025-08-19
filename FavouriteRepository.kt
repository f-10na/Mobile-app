package com.example.myapplication.data.favourites

import androidx.lifecycle.LiveData
import com.example.myapplication.utils.RoomBook

class FavouriteRepository (private val favouriteDao: FavouriteDao) {
    val allFavouriteBooks: LiveData<List<RoomBook>> = favouriteDao.getAllFavourites()

    suspend fun insert(roomBook: RoomBook) {
        favouriteDao.insert(roomBook)
    }

    suspend fun update(roomBook: RoomBook) {
        favouriteDao.update(roomBook)
    }

    suspend fun delete(roomBook: RoomBook) {
        favouriteDao.delete(roomBook)
    }

    fun getFavouriteItem(id: Int): LiveData<RoomBook> {
        return favouriteDao.getFavouriteItem(id)
    }

    suspend fun hasUserLikedBook(email: String?, bookId: String): Boolean {
        val count = email?.let { favouriteDao.hasUserLikedBook(it, bookId) } ?: 0
        return count > 0
    }

}