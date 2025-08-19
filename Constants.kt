package com.example.myapplication.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import com.example.myapplication.NavItem

object Constants {
    val NavItems = listOf(
        NavItem(
            icon = Icons.Filled.Home,
            route = "home"
        ),
        NavItem(
            icon = Icons.Filled.Search,
            route = "search"
        ),
        NavItem(
            icon = Icons.Filled.AccountCircle,
            route = "profile"
        )
    )
}