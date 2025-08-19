package com.example.myapplication.data.reviews

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MyReviews")
data class ReviewTable(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bookId: String,
    val content: String,
    val title: String
)