package com.example.myapplication

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.myapplication.utils.FirebaseBook


@Composable
fun Search(viewModel: MyViewModel, navController: NavController) {
    val topBookUiState by viewModel.topBookUiState.collectAsState()
    val genreUiState by viewModel.genreUiState.collectAsState()
    val context = LocalContext.current
    val searchText by viewModel.searchText.observeAsState("")
    var showDialog by remember { mutableStateOf(false) }
    var selectedGenre by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Column {
        TextField(
            value = searchText,
            onValueChange = { viewModel.updateSearchText(it) },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFFF0AAAD)
            )
        ) {
            Text(text = "Find Nearby Book Stores")
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Confirmation") },
                text = { Text("Do you want to open the website and leave the app?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            //Send custom broadcast
                            val broadcastIntent = Intent("com.example.myapplication.CUSTOM_ACTION")
                            context.sendBroadcast(broadcastIntent)
                            showDialog = false
                            val url = "https://www.waterstones.com/bookshops?lat=51.5072178&lng=-0.1275862&facility=&search=$searchText"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
        Text(
            text = "Suggested Books",
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 20.sp,
                color = Color(0xFFF0AAAD)
            ),
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )
        LazyRow(
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topBookUiState.suggestedBooks) { book ->
                SuggestedBookItem(book) {
                    navController.navigate("bookOverview/${book.bookId}")
                }
            }
        }
        // Dropdown Menu for Genres
        Box(modifier = Modifier.padding(10.dp)) {
            Text(
                text = selectedGenre.ifEmpty { "Select Genre" },
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif,
                    fontSize = 20.sp,
                    color = Color(0xFFF0AAAD)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(16.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(text = { Text("All") },onClick = {
                    selectedGenre = ""
                    expanded = false
                    viewModel.filterBooksByGenre("")
                })
                //populate the dropdown with dynamic list of genres
                genreUiState.genres.forEach { genre ->
                    DropdownMenuItem(text = { Text(genre) },onClick = {
                        selectedGenre = genre
                        expanded = false
                        viewModel.filterBooksByGenre(genre)
                    })
                }
            }
        }

// Define a LazyRow to display a horizontally scrolling list of items
        LazyRow(
            // Add padding around the content of the LazyRow
            contentPadding = PaddingValues(8.dp),
            // Set the horizontal arrangement with spacing of 8dp between items
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Define the items to be displayed in the LazyRow
            items(genreUiState.filteredBooks) { book ->
                // Call FilteredBookItem composable for each book in the list that matches the filter
                FilteredBookItem(book) {
                    // Handle the click event by navigating to the book overview screen
                    navController.navigate("bookOverview/${book.bookId}")
                }
            }
        }


    }
}



@Composable
fun SuggestedBookItem(book: FirebaseBook, onItemClick: () -> Unit) {
    val imageResource = ImageFunction.loadImage(book.bookId)
    val imagePainter = painterResource(id = imageResource)

    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun FilteredBookItem(book: FirebaseBook, onItemClick: () -> Unit) {
    val imageResource = ImageFunction.loadImage(book.bookId)
    val imagePainter = painterResource(id = imageResource)

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(400.dp)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Image(
                painter = imagePainter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
