package com.example.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class NetworkMonitor(private val context: Context) : DefaultLifecycleObserver {
    private lateinit var viewModel: MyViewModel
    // Get an instance of ConnectivityManager
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Define a network callback to handle network changes
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // Method called when the network becomes available
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Toast.makeText(context, "Network Connected", Toast.LENGTH_SHORT).show()
        }

        // Method called when the network is lost
        override fun onLost(network: Network) {
            super.onLost(network)
            Toast.makeText(context, "Network Disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    // Register the network callback
    fun register() {
        val networkRequest = NetworkRequest.Builder()
            // Add a capability to request an internet network
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // Unregister the network callback
    fun unregister() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    // Lifecycle method called when the associated lifecycle owner is started
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        register() // register the network callback
    }

    // Lifecycle method called when the associated lifecycle owner is stopped
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        unregister() // Unregister the network callback
    }
}