package com.example.myapplication

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(private val context: Context) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Toast.makeText(context, "App in Foreground", Toast.LENGTH_SHORT).show()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Toast.makeText(context, "App in Background", Toast.LENGTH_SHORT).show()
    }
}