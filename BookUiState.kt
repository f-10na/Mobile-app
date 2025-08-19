package com.example.myapplication.utils

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplication.data.readingList.ReadingListDao

data class FirebaseBook(
    val docId :String = "",
    val author: String = "",
    val bookId: String = "",
    val genre: String = "",
    var likes: Int = 0,
    var numberOfReviews: Int = 0,
    val synopsis: String = "",
    val title: String = ""
)

@Entity(tableName = "MyFavourites")
data class RoomBook(
    @PrimaryKey val id: String,
    val title: String,
    val user: String
)

@Entity(tableName = "MyReadingList")
data class ReadingListBook(
    @PrimaryKey val id: String,
    val title: String,
    val user: String
)

data class BookUiState(
    val firebaseBooks: List<FirebaseBook> = emptyList(),
    val suggestedBooks: List<FirebaseBook> = emptyList(),
    val roomBooks: List<RoomBook> = emptyList(),
    val ids: List<String> = emptyList(),
    val readingList: List<ReadingListBook> = emptyList(),
    val numberOfLikes: List<Int> = emptyList(),
    val numberOfReviews: List<Int> = emptyList(),
)

data class TopBookUiState(
    val suggestedBooks: List<FirebaseBook> = emptyList()
)

data class GenreUiState(
    val genres: List<String> = emptyList(),
    val filteredBooks: List<FirebaseBook> = emptyList()
)
