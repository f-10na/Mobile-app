package com.example.myapplication


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.dialog.Dialog
import com.example.myapplication.utils.ReadingListBook
import com.example.myapplication.utils.Review
import com.example.myapplication.utils.RoomBook
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BookOverviewScreen(navController: NavHostController, viewModel: MyViewModel, bookId: String?) {

    var likes = 0
    // Fetch and display the book details using the bookId
    val book = bookId?.let { viewModel.getBookById(it) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val reviewUiState by viewModel.reviewUiState.collectAsState()
    var createReviewModal by remember { mutableStateOf(false) }

    Column {
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .align(Alignment.Start)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = Color(0xFFF0AAAD),
                modifier = Modifier.size(37.dp),
                contentDescription = "Back"
            )
        }

        if (book != null) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                val imageResource = ImageFunction.loadImage(book.bookId)
                val imagePainter = painterResource(id = imageResource)
                //display book image
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFF0AAAD)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Author: ${book.author}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Genre: ${book.genre}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    //display book info
                    Text(text = book.synopsis, style = MaterialTheme.typography.bodyMedium)

                            Row(horizontalArrangement = Arrangement.End) {
                                var imageVector by remember { mutableStateOf(Icons.Default.FavoriteBorder) }
                                // Check if the user is logged in and update the favourite icon accordingly
                                if (currentUser != null) {
                                    viewModel.hasUserLikedBook( bookId) { isLiked ->
                                            imageVector = if (isLiked) {
                                                // Item is liked, set to filled heart icon
                                                Icons.Default.Favorite
                                            } else {
                                                // Item is not liked, set to outline heart icon
                                                Icons.Default.FavoriteBorder
                                            }
                                    }
                                }
                                // Function to handle click event
                                val onFavoriteClick: () -> Unit = {

                                    if (currentUser?.email != null) {
                                        val roomBook = RoomBook(bookId,book.title,
                                            currentUser.email!!
                                        )
                                        //!isLiked
                                        viewModel.hasUserLikedBook(bookId) { isLiked ->
                                            imageVector = if (isLiked) {
                                                // Item is liked, set to filled heart icon
                                                viewModel.removeFromFavorites(roomBook)
                                                viewModel.decrementFavouriteCount(roomBook)
                                                Icons.Default.FavoriteBorder
                                            } else {
                                                // Item is not liked, set to outline heart icon
                                                viewModel.addToFavorites(roomBook)
                                                viewModel.incrementFavouriteCount(roomBook)

                                                Icons.Default.Favorite
                                            }
                                        }
                                    }else{
                                        Toast.makeText(context,"NOT LOGGED IN,CAN'T ADD TO FAVOURITES",Toast.LENGTH_SHORT).show()
                                    }
                                }
                                IconLayout(onClick = onFavoriteClick, imageVector = imageVector)


                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            // Button to show bottom sheet
                            Button(
                                onClick = { showBottomSheet = true },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(20.dp)
                                    .width(300.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0AAAD))
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = null)
                                Text(text = "Like, Log, Review, Add to List + more")
                            }
                            Text(
                                text = "REVIEWS",
                                style = MaterialTheme.typography.titleMedium
                            )
                            viewModel.fetchReviews(bookId)
                            LazyColumn(
                                contentPadding = PaddingValues(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(reviewUiState.reviews) { review ->
                                    ReviewItem(review)
                                }
                            }
                        }
                    }
            }

        }
        if (showBottomSheet && book != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false }
            ) {

                    Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(80.dp),
                        modifier = Modifier.padding(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BottomSheetItem(
                            onClick = {vibrate(context)
                                if (currentUser != null) {
                                currentUser.email?.let {
                                    ReadingListBook(bookId,book.title,
                                        it
                                    )
                                }?.let { viewModel.addToReadingList(readingListBook = it) }
                                } },
                            vector = Icons.Default.Add
                        )
                        BottomSheetItem(
                            onClick = { if (currentUser != null) {
                                viewModel.hasUserReviewedBook( bookId) { isReviewed ->
                                    Log.d(TAG,isReviewed.toString())
                                    createReviewModal = !isReviewed
                                }
                            }},
                            vector = Icons.Default.Create
                        )
                        BottomSheetItem(
                            onClick = { val bookTitle: String = book.title
                                val bookAuthor: String = book.author
                                val bookDescription: String = book.synopsis
                                val message = """
                            Check out this book!
                            Title: $bookTitle
                            Author: $bookAuthor
                            Description: $bookDescription
                            """.trimIndent()

                                // Create the intent
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, message) // Add the book details
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent) },
                            vector = Icons.Default.Share
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(20.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        showBottomSheet = false
                                    }
                                }
                            }, modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(13.dp)
                                .width(300.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0AAAD))
                        ) {
                            Text("Hide")
                        }
                    }
                }
            }
        }
    if (createReviewModal) {
        AddReviewModal(viewModel, onDismiss = { createReviewModal = false } , bookId,context )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ReviewItem(review : Review) {
    val context = LocalContext.current
    // State to track whether the review content is expanded or not
    val isExpanded = remember { mutableStateOf(false) }
    val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // Expand the review content when long press is detected
                isExpanded.value = !isExpanded.value
            }
        })

    Card(
        modifier = Modifier
            .padding(8.dp)
            .pointerInteropFilter { event ->
                gestureDetector.onTouchEvent(event)
                true
            },elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Row(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(13.dp)
                .width(300.dp)){
                Icon(imageVector = Icons.Filled.AccountBox, contentDescription = null)
                review.title?.let {
                    Text(
                        text = it, // Assuming authorName is a property of Review
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 20.sp,
                            color = Color.Black, // Example color, adjust as needed
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Start
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            review.content?.let {
                Text(
                    text = if (isExpanded.value) it else it.take(50) + "...",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            review.email?.let{
                Text(
                    text = it,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun BottomSheetItem(onClick: () -> Unit,vector: ImageVector) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(vertical = 8.dp)
        ){
            Icon(
                imageVector = vector,
                tint = Color(0xFFF0AAAD),
                modifier = Modifier.size(37.dp),
                contentDescription = null
            )
        }
}


@Composable
fun IconLayout(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    imageVector: ImageVector
) {

    // Display the favourite icon
    Icon(
        imageVector = imageVector,
        contentDescription = "",
        modifier = modifier
            .size(30.dp)
            .clickable { onClick.invoke() },
        tint = Color(0xFFF0AAAD)
    )
}

@Composable
fun AddReviewModal(viewModel: MyViewModel, onDismiss: () -> Unit, bookId: String?, context: Context ) {
    var reviewText by remember { mutableStateOf("") }
    var reviewTitle by remember { mutableStateOf("") }

    // Function to dismiss the dialog
    fun dismiss() {
        onDismiss()
    }
    Dialog(showDialog = true,onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
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
                    IconButton(onClick = {dismiss()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
                Text(text = "Add a Review", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reviewTitle,
                    onValueChange = { reviewTitle = it },
                    label = { Text("Your Review Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Your Review") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // Add the review to the database
                        if (currentUser != null) {
                            currentUser.email?.let {
                                if (bookId != null) {
                                    viewModel.addReview(it, bookId, reviewText, reviewTitle)
                                    viewModel.addRoomReview(bookId,reviewText,reviewTitle)
                                }
                            }
                            Toast.makeText(context, "Review added", Toast.LENGTH_SHORT).show()
                        }
                        if (currentUser != null) {
                            Log.d(TAG, currentUser.email.toString()+bookId)
                        }

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

fun vibrate(context: Context) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val vibrator = vibratorManager.defaultVibrator
    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
}