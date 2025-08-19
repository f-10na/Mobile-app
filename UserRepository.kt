package com.example.myapplication.data.users

import androidx.lifecycle.LiveData
import com.example.myapplication.data.users.User
import com.example.myapplication.data.users.UserDao

class UserRepository(private val userDao: UserDao) {
    fun getUser(userId: String): LiveData<User> {
        return userDao.getUser(userId)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}