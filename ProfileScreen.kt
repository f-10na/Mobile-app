package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfileScreen(viewModel: MyViewModel, navController: NavController) {
    val context = LocalContext.current
    val permissionGranted = remember { mutableStateOf(viewModel.isVibrationPermissionGranted(context)) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        permissionGranted.value = isGranted
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Profile",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 40.sp,
                color = Color(0xFFF0AAAD),
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(14.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {navController.navigate("myReviews")},
            modifier = Modifier.padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0AAAD))
        ) { Text(text = "My Reviews") }
        Button(
            onClick = {navController.navigate("myFavourites")},
            modifier = Modifier.padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0AAAD))
        ) { Text(text = "My Favourites") }
        Button(
            onClick = {navController.navigate("myReadingList")},
            modifier = Modifier.padding(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0AAAD)))
        {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Read Icon"
            )
            Text(text = "My Reading List")
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(220.dp))
        if (currentUser != null) {
            Text(text = currentUser.email.toString())
        }
        Row(modifier = Modifier.padding(vertical = 16.dp)){
            Text(text = "Logout",modifier = Modifier.clickable { auth.signOut()
                // Navigate to the sign-in page using intent
                val intent = Intent(context, SignIn::class.java)
                context.startActivity(intent)
                // Finish the current activity to prevent the user from going back
                (context as? Activity)?.finish()})
            IconButton(
                onClick = {
                    //logs user out and ensures they have to login to get back into the app
                    auth.signOut()
                    viewModel.saveLoginState(false)
                    viewModel.clearUserData()
                    // Navigate to the sign-in page using intent
                    val intent = Intent(context, SignIn::class.java)
                    context.startActivity(intent)
                    // Finish the current activity to prevent the user from going back
                    (context as? Activity)?.finish()
                    (context as? AppCompatActivity)?.finish()
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .padding(end = 8.dp)
                ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout"
                )
                }
            }


    }
}


