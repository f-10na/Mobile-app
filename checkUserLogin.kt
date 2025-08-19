package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController

class CheckUserLogin : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: MyViewModel = viewModel()
            if (!viewModel.isLoggedIn()) {
                // User is not logged in, go to Sign in Page
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish() // Close the MainActivity
            } else {
                // User is logged in, go to Home
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the MainActivity
            }
        }
    }
}