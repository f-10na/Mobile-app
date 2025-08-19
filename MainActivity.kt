package com.example.myapplication

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigation
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.BottomNavigationItem
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.Constants

class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var appLifecycleObserver: AppLifecycleObserver
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Vibration permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Vibration permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Register the receiver
            networkMonitor = NetworkMonitor(this)
            networkMonitor.register()

            // Register the lifecycle observer
            appLifecycleObserver = AppLifecycleObserver(this)
            lifecycle.addObserver(appLifecycleObserver)
            lifecycle.addObserver(networkMonitor)
            //remember navController so it doesn't get recreated on recomposition
            val navController = rememberNavController()
            // If the user is not logged in, navigate to the SignIn screen
            if (!viewModel.isLoggedIn()) {
                val intent = Intent(this, SignIn::class.java)
                startActivity(intent)
                finish() // Close the MainActivity
                return@setContent
            }

            MyApplicationTheme {
                viewModel.getMyReadingList()
                viewModel.getMyFavourites()
                viewModel.getMyReviews()
                viewModel.bookUiState
                viewModel.reviewUiState
                MyReadingList(navController, viewModel)
                MyFavourites(navController, viewModel)
                MyReviews(navController, viewModel)
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Scaffold Component
                    Scaffold(
                        //bottom navigation
                        bottomBar = {
                            BottomNavigationBar(navController = navController)
                        }, content = { padding ->
                            //NavHost: where the screens are placed
                            NavHostContainer(
                                navController = navController,
                                padding = padding,
                                viewModel = viewModel
                            )
                        }
                    )

                }
            }


        }
    }


    override fun onStart() {
        super.onStart()
        Log.v("Activity Lifecycle Methods", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.v("Activity Lifecycle Methods", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v("Activity Lifecycle Methods", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.v("Activity Lifecycle Methods", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Activity Lifecycle Methods", "onDestroy")
        // Unregister the receiver
        networkMonitor.unregister()

        // Remove the lifecycle observer
        lifecycle.removeObserver(appLifecycleObserver)
    }
}


@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues,
    viewModel: MyViewModel
){
    NavHost(
        navController = navController,

        //set the start destination as home-screen
        startDestination = "home",

        //set padding provided by scaffold
        modifier = Modifier.padding(paddingValues = padding),

        builder = {
            //route : Home
            composable("home"){
                HomeScreen(viewModel,navController)
            }
            //route : Search
            composable("search"){ backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                Search(viewModel,navController)
            }
            //route : Profile Screen
            composable("profile"){
                ProfileScreen(viewModel,navController)
            }//route : Book Overview Screen
            composable("bookOverview/{bookId}") { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookOverviewScreen(navController,viewModel, bookId)
            }//route: My Favourites Screen
            composable("myFavourites"){
                MyFavourites(navController,viewModel)
            }//route: My Reviews Screen
            composable("myReviews"){
                MyReviews(navController,viewModel)
            }//route: Reading List
            composable("myReadingList"){
                MyReadingList(navController,viewModel)
            }
        }
    )
}

@Composable
fun BottomNavigationBar (navController: NavHostController){

    BottomNavigation(
        //set background color
        backgroundColor = Color(0xFFFFFFFF)
    ){
        //observe the backstack
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        //observe current route to change icon etc when navigated
        val currentRoute = navBackStackEntry?.destination?.route

        //Bottom nav items i declared
        Constants.NavItems.forEach { navItem ->
            //Place bottom nav items
            BottomNavigationItem(
                //if currentRoute is equal then its selected route
                selected = currentRoute == navItem.route,

                //navigate on click
                onClick = {
                    navController.navigate(navItem.route){
                        // Avoid multiple copies of the same destination in the back stack
                        launchSingleTop = true
                        // Restore state when reelecting a previously selected item
                        restoreState = true
                        // Pop up to the start destination of the graph to avoid building up a large stack of destinations
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                //Icon of navItem
                icon = {
                    Icon(imageVector = navItem.icon, contentDescription ="route" ,tint = Color(0xFFF0AAAD))
                }
            )
        }

    }

}

