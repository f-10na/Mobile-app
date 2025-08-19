package com.example.myapplication.data.favourites

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.utils.RoomBook

@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roomBook: RoomBook)

    @Update
    suspend fun update(roomBook: RoomBook)

    @Delete
    suspend fun delete(roomBook: RoomBook)

    @Query("SELECT * from MyFavourites WHERE id = :id")
    fun getFavouriteItem(id: Int): LiveData<RoomBook>

    @Query("SELECT * FROM MyFavourites")
    fun getAllFavourites(): LiveData<List<RoomBook>>

    @Query("SELECT COUNT(*) FROM MyFavourites WHERE user = :email AND id = :bookId")
    suspend fun hasUserLikedBook(email: String, bookId: String): Int

}
