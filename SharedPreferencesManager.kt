package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

// Class to manage SharedPreferences for user authentication checks
class SharedPreferencesManager(context: Context) {
    // Initialize SharedPreferences with the name "MyAppPrefs" in private mode
    private val prefs: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // Function to save the login state in SharedPreferences
    fun saveLoginState(isLoggedIn: Boolean) {
        // Get an editor to modify the SharedPreferences
        val editor = prefs.edit()
        // Put the boolean value representing the login state into the SharedPreferences
        editor.putBoolean("isLoggedIn", isLoggedIn)
        // Apply the changes asynchronously
        editor.apply()
    }

    // Function to check the login state from SharedPreferences
    fun isLoggedIn(): Boolean {
        // Retrieve the boolean value representing the login state, defaulting to false if not found
        return prefs.getBoolean("isLoggedIn", false)
    }
}