package com.example.myapplication.utils

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Review(
    val email: String? = "",
    val bookId: String? = null,
    val content: String? = "",
    val title: String? = ""
)

@Entity(tableName = "MyReviews")
data class RoomReview(
    @PrimaryKey val id: String,
    val content: String,
    val title: String
)

data class ReviewUiState(
    val reviews: List<Review> = emptyList(),
    val myReviews: List<RoomReview> = emptyList()
)