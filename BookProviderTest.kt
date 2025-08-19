package com.example.myapplication.bookProvider
//
//import android.content.ContentValues
//import android.net.Uri
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import androidx.test.platform.app.InstrumentationRegistry
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class BookProviderTest {
//
//    private val contentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
//
//    @Before
//    fun setUp() {
//        // Set up any necessary preconditions here
//    }
//
//    @Test
//    fun testInsertAndQuery() {
//        val contentValues = ContentValues().apply {
//            put("title", "To Kill a Mockingbird")
//            put("author", "Harper Lee")
//        }
//
//        val uri: Uri? = contentResolver.insert(BookProvider.CONTENT_URI, contentValues)
//        assertNotNull(uri)
//
//        val cursor = contentResolver.query(
//            BookProvider.CONTENT_URI,
//            null,
//            null,
//            null,
//            null
//        )
//
//        assertNotNull(cursor)
//        cursor?.use {
//            assertTrue(it.moveToFirst())
//            val title = it.getString(it.getColumnIndexOrThrow("title"))
//            val author = it.getString(it.getColumnIndexOrThrow("author"))
//
//            assertEquals("To Kill a Mockingbird", title)
//            assertEquals("Harper Lee", author)
//        }
//    }
//}