package com.example.myapplication.data.reviews

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.utils.RoomReview


@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(roomReview: RoomReview)

    @Update
    suspend fun update(roomReview: RoomReview)

    @Query("UPDATE MyReviews SET title = :newTitle, content = :newContent WHERE id = :id")
    suspend fun updateReview(id: String, newTitle: String, newContent: String)

    @Delete
    suspend fun delete(roomReview: RoomReview)

    @Query("SELECT * from MyReviews WHERE id = :id")
    fun getReviewItem(id: String): LiveData<RoomReview>

    @Query("SELECT * FROM MyReviews")
    fun getAllReviews(): LiveData<List<RoomReview>>


    @Query("SELECT COUNT(*) FROM MyReviews WHERE id = :bookId")
    suspend fun hasUserReviewedBook(bookId: String): Int
}