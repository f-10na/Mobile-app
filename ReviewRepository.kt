package com.example.myapplication.data.reviews

import androidx.lifecycle.LiveData
import com.example.myapplication.utils.RoomReview

class ReviewRepository (private val reviewDao: ReviewDao) {
    val allReviews: LiveData<List<RoomReview>> = reviewDao.getAllReviews()

    suspend fun insert(roomReview: RoomReview) {
        reviewDao.insert(roomReview)
    }

    suspend fun update(roomReview: RoomReview) {
        reviewDao.update(roomReview)
    }

    suspend fun updateReview(id: String, newTitle: String, newContent: String) {
        reviewDao.updateReview(id, newTitle, newContent)
    }

    suspend fun delete(roomReview: RoomReview) {
        reviewDao.delete(roomReview)
    }

    fun getReviewItem(id: String): LiveData<RoomReview> {
        return reviewDao.getReviewItem(id)
    }

    suspend fun hasUserReviewedBook(bookId: String): Boolean {
        val count = reviewDao.hasUserReviewedBook(bookId)
        return count > 0
    }

}