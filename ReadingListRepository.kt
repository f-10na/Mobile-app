package com.example.myapplication.data.readingList

import androidx.lifecycle.LiveData
import com.example.myapplication.utils.ReadingListBook

class ReadingListRepository (private val readingListDao: ReadingListDao) {
    val readingList: LiveData<List<ReadingListBook>> = readingListDao.getReadingList()

    suspend fun insert(readingListBook: ReadingListBook) {
        readingListDao.insert(readingListBook)
    }

    suspend fun update(readingListBook: ReadingListBook) {
        readingListDao.update(readingListBook)
    }

    suspend fun delete(readingListBook: ReadingListBook) {
        readingListDao.delete(readingListBook)
    }

    fun getReadingListItem(id: String): LiveData<ReadingListBook> {
        return readingListDao.getReadingListItem(id)
    }

}