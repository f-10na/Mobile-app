package com.example.myapplication.data.users

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val isLoggedIn: Boolean
)
