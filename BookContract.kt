package com.example.myapplication.bookProvider

import android.net.Uri


object BookContract {
    // Content provider authority
    const val AUTHORITY = "com.example.myapplication.provider"

    // Base content URI
    val BASE_CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY")

    // Path to the "books" table
    const val PATH_BOOKS = "books"

    // Define content URIs for books
    val CONTENT_URI: Uri = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BOOKS).build()

    // Book table columns
    object BookEntry {
        const val TABLE_NAME = "books"
        const val COLUMN_ID = "_id"
        const val COLUMN_TITLE = "title"
    }
}