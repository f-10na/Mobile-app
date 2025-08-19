package com.example.myapplication.data.readingList

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.utils.ReadingListBook

@Dao
interface ReadingListDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(readingListBook: ReadingListBook)

    @Update
    suspend fun update(readingListBook: ReadingListBook)

    @Delete
    suspend fun delete(readingListBook: ReadingListBook)

    @Query("SELECT * from MyReadingList WHERE id = :id")
    fun getReadingListItem(id: String): LiveData<ReadingListBook>

    @Query("SELECT * FROM MyReadingList")
    fun getReadingList(): LiveData<List<ReadingListBook>>
}
