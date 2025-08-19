package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.utils.RoomBook
import kotlin.math.roundToInt

@Composable
fun MyFavourites(navController: NavHostController, viewModel: MyViewModel) {
    val bookUiState by viewModel.bookUiState.collectAsState()
    LocalContext.current
    viewModel.getMyFavourites()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(15.dp)) {
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    tint = Color(0xFFF0AAAD),
                    modifier = Modifier.size(37.dp),
                    contentDescription = "Back"
                )
            }
            Text(
                text = "My Favourites",
                style = TextStyle(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 40.sp,
                    color = Color(0xFFF0AAAD),
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .width(350.dp)
                    .height(35.dp)
                    .background(Color.White)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Swipe Left To Delete",
                    color = Color(0xFFF0AAAD),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .wrapContentSize()
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookUiState.roomBooks) { roomBook ->
                // Call MyFavouriteItem composable for each book in the list
                MyFavouriteItem(
                    roomBook = roomBook,
                    // Pass the current book to the composable
                    onDelete = {viewModel.removeFromFavorites(roomBook) // Remove the book from  user's favorites
                viewModel.decrementFavouriteCount(roomBook)})  //Decrement count in Firebase
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // Opt-in to use experimental Material 3 API
@Composable
fun MyFavouriteItem(
    roomBook: RoomBook,
    onDelete: (RoomBook) -> Unit
) {
    // Remember the offsetX state for horizontal dragging
    var offsetX by remember { mutableFloatStateOf(0f) }
    // Define the swipe threshold for deleting an item
    val swipeThreshold = -300f

    // Define a Box composable
    Box(
        modifier = Modifier
            .fillMaxWidth() // Fill the maximum width available
            .padding(8.dp)
            .height(100.dp)
            .pointerInput(Unit) {
                // Detect horizontal drag gestures
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // Check if the drag gesture exceeds the swipe threshold
                        if (offsetX < swipeThreshold) {
                            // Call onDelete if dragged left beyond the threshold
                            onDelete(roomBook)
                        }
                        // Reset offsetX after dragging ends
                        offsetX = 0f
                    }
                ) { change, dragAmount ->
                    change.consume() // Consume the drag gesture
                    // Update offsetX within the range [-500, 0] based on drag amount
                    offsetX = (offsetX + dragAmount).coerceIn(-500f, 0f)
                }
            }
    ) {
        // Define a Card composable inside the Box
        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) }, // Offset the Card based on offsetX
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Display the book title
                Text(text = roomBook.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
