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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.example.myapplication.utils.ReadingListBook
import kotlin.math.roundToInt


@Composable
fun MyReadingList(navController: NavHostController, viewModel: MyViewModel,) {
    val bookUiState by viewModel.bookUiState.collectAsState()
    LocalContext.current
    viewModel.getMyReadingList()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.padding(15.dp)){
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
                text = "My Reading List",
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
//This lazy column displays all books in a user's reading list and is populated using Room Database
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bookUiState.readingList) {readingListBook ->
                MyReadingListItem(readingListBook,onDelete = {
                    //uses swipe event to remove from reading list
                    viewModel.removeReadingList(readingListBook)
                })
            }

        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReadingListItem(
    readingListBook: ReadingListBook,
    onDelete: (ReadingListBook) -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp)
            .pointerInput(Unit) {
                // Detect horizontal drag gestures
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // Handle the end of the drag gesture
                        offsetX = when {
                            // If dragged right beyond -300 pixels, trigger delete and reset offset
                            offsetX > -300 -> {
                                onDelete(readingListBook)
                                0f
                            }

                            else -> 0f
                        }
                    }
                ) { change, dragAmount ->
                    // Consume the drag gesture event
                    change.consume()
                    offsetX = (offsetX + dragAmount).coerceIn(-500f, 500f)
                }
            }
    ) {
        // Check if the offsetX is less than -300, then trigger delete and reset offset
        if (offsetX < -300) {
            onDelete(readingListBook)
            offsetX = 0f
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color(0xFFF0AAAD),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.roundToInt(), 0) },
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = readingListBook.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
