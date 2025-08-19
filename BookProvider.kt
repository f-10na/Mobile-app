package com.example.myapplication.bookProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import androidx.room.Room
import com.example.myapplication.data.books.Book
import com.example.myapplication.data.books.BookDatabase

class BookProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.myapplication.provider"
        const val BOOKS_TABLE = "books"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$BOOKS_TABLE")
        const val BOOKS_URI_CODE = 1

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, BOOKS_TABLE, BOOKS_URI_CODE)
        }
    }

    private lateinit var bookDatabase: BookDatabase

    override fun onCreate(): Boolean {
        bookDatabase = Room.databaseBuilder(
            context!!,
            BookDatabase::class.java, "books.db"
        ).build()
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor: Cursor?
        when (uriMatcher.match(uri)) {
            BOOKS_URI_CODE -> {
                cursor = bookDatabase.bookDao().getAllBooks()
                cursor.setNotificationUri(context?.contentResolver, uri)
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
        return cursor
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        when (uriMatcher.match(uri)) {
            BOOKS_URI_CODE -> {
                val id = values?.let {
                    Book(
                        id = 0,
                        title = it.getAsString("title"),
                        author = it.getAsString("author")
                    )
                }?.let {
                    bookDatabase.bookDao().insertBook(it)
                }
                context?.contentResolver?.notifyChange(uri, null)
                return Uri.withAppendedPath(CONTENT_URI, id.toString())
            }
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("Update operation is not supported yet")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("Delete operation is not supported yet")
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            BOOKS_URI_CODE -> "vnd.android.cursor.dir/$AUTHORITY.$BOOKS_TABLE"
            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }
    }
}