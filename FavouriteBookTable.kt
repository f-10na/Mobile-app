package com.example.myapplication.data.favourites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MyFavourites")
data class FavouriteBookTable(
    @PrimaryKey val id: String,
    val user: String,
    val title: String
)
