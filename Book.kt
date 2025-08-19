package com.example.myapplication.data.books

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val title: String,
    val author: String
)
