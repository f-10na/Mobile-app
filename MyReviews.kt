package com.example.myapplication


import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.utils.RoomReview
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.dialog.Dialog
import kotlin.math.roundToInt

@Composable
fun MyReviews(navController: NavHostController, viewModel: MyViewModel) {
    val reviewUiState by viewModel.reviewUiState.collectAsState()
    val context = LocalContext.current
    viewModel.getMyReviews()

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
                text = "My Reviews",
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
                    text = "Swipe Left To Edit and Right to Delete",
                    color = Color(0xFFF0AAAD),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .wrapContentSize()
                )
            }
        }

        var showEditModal by remember { mutableStateOf(false) }
        var selectedReviewId by remember { mutableStateOf<String?>(null) }

        //lazy column populated with reviews a user has created
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        items(reviewUiState.myReviews) { review ->
            MyReviewItem(
                roomReview = review,
                onEdit = { selectedReviewId = review.id
                    showEditModal = true},
                onDelete = {
                    viewModel.removeReview(review.id, review)
                }
                )
            }

        }
        //modal triggered if condition set to true by swipe event
        if (showEditModal && selectedReviewId != null) {
            EditReviewModal(
                viewModel = viewModel,
                onDismiss = { showEditModal = false },
                reviewId = selectedReviewId!!,
                context = context
            )
        }
    }
}

//review element in lazy column with 2 swipe events
@Composable
fun MyReviewItem(roomReview: RoomReview, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Remember the offsetX state for horizontal dragging
    var offsetX by remember { mutableFloatStateOf(0f) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Gray)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                // Detect horizontal drag gestures
                detectHorizontalDragGestures(
                    onDragEnd = {
                        // Determine action based on offsetX value when dragging ends
                        offsetX = when {
                            offsetX > 300 -> {
                                onDelete() // Call onDelete if dragged right beyond 300 pixels
                                0f // Reset offsetX
                            }

                            offsetX < -300 -> {
                                onEdit() // Call onEdit if dragged right beyond -300 pixels
                                0f // Reset offsetX
                            }

                            else -> 0f // Reset offsetX if drag distance is insufficient
                        }
                    }
                ) { change, dragAmount ->
                    change.consume() // Consume the drag gesture
                    offsetX = (offsetX + dragAmount).coerceIn(-500f, 500f)
                }
            },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = roomReview.title, style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = roomReview.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun EditReviewModal(viewModel: MyViewModel, onDismiss: () -> Unit, reviewId: String, context: Context) {
    val review by viewModel.getReviewItem(reviewId).observeAsState()

    // State for review title and content
    var reviewTitle by remember { mutableStateOf("") }
    var reviewText by remember { mutableStateOf("") }

    // Update state when review data is available
    LaunchedEffect(review) {
        review?.let {
            reviewTitle = it.title
            reviewText = it.content
        }
    }
    Dialog(showDialog = true,onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(color = Color.Transparent)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { onDismiss() }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                Text(text = "Edit Review", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reviewTitle,
                    //when updating review retrieves what is in this field and maps it to title
                    onValueChange = { reviewTitle = it },
                    label = { Text("Your Review Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reviewText,
                    //when updating review retrieves what is in this field and maps it to content
                    onValueChange = { reviewText = it },
                    label = { Text("Your Review") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Add the review to the database
                        viewModel.editReview(reviewId, reviewTitle, reviewText)
                        if (currentUser != null) {
                            currentUser.email?.let {
                                //updates both room and firebase explained in view-model
                                viewModel.updateReview(reviewId,reviewText,
                                    it,reviewTitle)
                            }
                        }
                        Toast.makeText(context, "Review edited", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Submit")
                }
            }
        }
    }
}