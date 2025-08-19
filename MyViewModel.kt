package com.example.myapplication

import android.Manifest
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.favourites.FavouriteDatabase
import com.example.myapplication.data.favourites.FavouriteRepository
import com.example.myapplication.data.readingList.ReadingListDatabase
import com.example.myapplication.data.readingList.ReadingListRepository
import com.example.myapplication.data.reviews.ReviewDatabase
import com.example.myapplication.data.reviews.ReviewRepository
import com.example.myapplication.data.users.User
import com.example.myapplication.data.users.UserDatabase
import com.example.myapplication.data.users.UserRepository
import com.example.myapplication.utils.BookUiState
import com.example.myapplication.utils.FirebaseBook
import com.example.myapplication.utils.GenreUiState
import com.example.myapplication.utils.ReadingListBook
import com.example.myapplication.utils.Review
import com.example.myapplication.utils.ReviewUiState
import com.example.myapplication.utils.RoomBook
import com.example.myapplication.utils.RoomReview
import com.example.myapplication.utils.TopBookUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Initialize FirebaseAuth
val auth: FirebaseAuth = FirebaseAuth.getInstance()
// Get the current user
val currentUser = auth.currentUser


class  MyViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferencesManager = SharedPreferencesManager(application)

    //initialise Firestore
    private val db = Firebase.firestore

    //Expose screen UI states
    private val _uiBookState = MutableStateFlow(BookUiState())
    private val _uiReviewState = MutableStateFlow(ReviewUiState())
    private val _uiTopBooksState = MutableStateFlow(TopBookUiState())
    private val _uiGenreState = MutableStateFlow(GenreUiState())

    val bookUiState: StateFlow<BookUiState> = _uiBookState.asStateFlow()
    val reviewUiState: StateFlow<ReviewUiState> = _uiReviewState.asStateFlow()
    val topBookUiState: StateFlow<TopBookUiState> = _uiTopBooksState.asStateFlow()
    val genreUiState: StateFlow<GenreUiState> = _uiGenreState.asStateFlow()

    //room initializers
    private val favouriteRepository: FavouriteRepository
    private val allFavouriteBooks: LiveData<List<RoomBook>>

    private val userRepository: UserRepository

    private val reviewRepository: ReviewRepository
    private val myReviews: LiveData<List<RoomReview>>

    private val readingListRepository: ReadingListRepository
    private val myReadingList: LiveData<List<ReadingListBook>>

    //save state of search text in search screen
    private val _searchText = MutableLiveData("")
    val searchText: LiveData<String> get() = _searchText



    init {
        val favouriteDao = FavouriteDatabase.getDatabase(application).favouriteDao()
        favouriteRepository = FavouriteRepository((favouriteDao))
        allFavouriteBooks = favouriteRepository.allFavouriteBooks

        val userDao = UserDatabase.getDatabase(application).userDao()
        userRepository = UserRepository((userDao))

        val reviewDao = ReviewDatabase.getDatabase(application).reviewDao()
        reviewRepository = ReviewRepository((reviewDao))
        myReviews = reviewRepository.allReviews

        val readingListDao = ReadingListDatabase.getDatabase(application).readingListDao()
        readingListRepository = ReadingListRepository((readingListDao))
        myReadingList = readingListRepository.readingList

        //onCreate/onStart app retrieves necessary data to fill user UI screen from Room and Firestore
        fetchBooks()
        fetchTopBooks()
        fetchGenres()
        getMyReviews()
        getMyFavourites()
        getMyReadingList()
    }

    // Function to update the search text
    fun updateSearchText(newText: String) {
        // Update the value of the _searchText property with the new text
        _searchText.value = newText
    }

    //BOOKS
    // fetch books from Firestore
    private fun fetchBooks() {
        db.collection("Books")
            .get()
            .addOnSuccessListener { result ->
                val books = mutableListOf<FirebaseBook>()
                for (document in result) {
                    val author = document.getString("Author") ?: ""
                    val bookId = document.getString("BookId") ?: ""
                    val genre = document.getString("Genre") ?: ""
                    val likes = document.getLong("NumberOfLikes")?.toInt() ?: 0
                    val numberOfReviews = document.getLong("NumberOfReviews")?.toInt() ?: 0
                    val synopsis = document.getString("Synopsis") ?: ""
                    val title = document.getString("Title") ?: ""
                    if (author.isNotBlank() && bookId.isNotBlank() && genre.isNotBlank() &&
                        synopsis.isNotBlank() && title.isNotBlank()
                    ) {
                        books.add(
                            FirebaseBook(
                                document.toString(),
                                author,
                                bookId,
                                genre,
                                likes,
                                numberOfReviews,
                                synopsis,
                                title
                            )
                        )
                    }
                }
                // Update the UI state with the fetched books
                _uiBookState.value = BookUiState(firebaseBooks = books)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching books", exception)
            }
    }

    //fetches top 3 books based on number of likes
    private fun fetchTopBooks() {
        db.collection("Books")
            .orderBy("NumberOfLikes", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { result ->
                val books = mutableListOf<FirebaseBook>()
                for (document in result) {
                    val author = document.getString("Author") ?: ""
                    val bookId = document.getString("BookId") ?: ""
                    val genre = document.getString("Genre") ?: ""
                    val likes = document.getLong("NumberOfLikes")?.toInt() ?: 0
                    val numberOfReviews = document.getLong("NumberOfReviews")?.toInt() ?: 0
                    val synopsis = document.getString("Synopsis") ?: ""
                    val title = document.getString("Title") ?: ""
                    if (author.isNotBlank() && bookId.isNotBlank() && genre.isNotBlank() &&
                        synopsis.isNotBlank() && title.isNotBlank()
                    ) {
                        books.add(
                            FirebaseBook(
                                document.id,
                                author,
                                bookId,
                                genre,
                                likes,
                                numberOfReviews,
                                synopsis,
                                title
                            )
                        )
                    }
                }
                // Update the UI state with the fetched books
                _uiTopBooksState.value = TopBookUiState(suggestedBooks = books)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching books", exception)
            }
    }

    //retrieves book id
    fun getBookById(bookId: String): FirebaseBook? {
        return _uiBookState.value.firebaseBooks.find { it.bookId == bookId }
    }

    //retrieves all unique genres to help populate a dropdown
    private fun fetchGenres() {
        viewModelScope.launch {
            try {
                val booksCollection = db.collection("Books")
                val genres = mutableSetOf<String>()

                val result = booksCollection.get().await()
                for (document in result) {
                    val genre = document.getString("Genre")
                    if (genre != null) {
                        genres.add(genre)
                    }
                }
                _uiGenreState.value = _uiGenreState.value.copy(genres = genres.toList())
            } catch (e: Exception) {
                // Handle exceptions if needed
            }
        }
    }

    // Function to filter books by a specific genre
    fun filterBooksByGenre(genre: String) {
        // Get the current list of all books from the UI state
        val allBooks = _uiBookState.value.firebaseBooks

        // Filter the books based on the provided genre
        // If the genre is empty, return all books
        // Otherwise, filter the books to include only those that match the genre
        val filteredBooks = if (genre.isEmpty()) {
            allBooks
        } else {
            allBooks.filter { it.genre == genre }
        }

        // Update the UI genre state with the filtered list of books
        _uiGenreState.value = _uiGenreState.value.copy(filteredBooks = filteredBooks)
    }


    //FAVOURITES
    // Function to check if the user has liked a specific book to choose the right heart icon onStart
    fun hasUserLikedBook(bookId: String, callback: (Boolean) -> Unit) {
        // Launch a new coroutine within the ViewModel scope
        viewModelScope.launch {
            // Check if the current user has liked the book with the given bookId
            val isLiked = favouriteRepository.hasUserLikedBook(currentUser?.email, bookId)
            // Execute the callback function with the result (true if the user has liked the book, false otherwise)
            callback(isLiked)
        }
    }

    // Function to get the user's favourite books
    fun getMyFavourites() {
        // Launch a new coroutine within the ViewModel scope
        viewModelScope.launch {
            // Access the flow of all favourite books from the repository
            favouriteRepository.allFavouriteBooks
                // Convert the list to a flow
                .asFlow()
                // Collect the flow of favourite books
                .collect { favouriteBooks ->
                    // Update the UI state with the list of favourite books
                    _uiBookState.update { currentState ->
                        // Copy the current state and set the roomBooks field to the list of favourite books
                        currentState.copy(roomBooks = favouriteBooks)
                    }
                }
        }
    }

    fun addToFavorites(roomBook: RoomBook) {
        viewModelScope.launch {
            favouriteRepository.insert(roomBook)
        }
    }

    fun removeFromFavorites(roomBook: RoomBook) {
        viewModelScope.launch {
            favouriteRepository.delete(roomBook)
        }
    }

    //whenever a user adds book to favourites it increments the number of likes field in firebase
    fun incrementFavouriteCount(roomBook: RoomBook) {
        viewModelScope.launch {
            try {
                //Get the Firestore document ID using the bookId
                val querySnapshot = db.collection("Books")
                    .whereEqualTo("BookId", roomBook.id)
                    .get()
                    .await()

                // Assuming there's only one document with this bookId
                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    //Update the numberOfLikes field
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(db.collection("Books").document(documentId))
                        val currentLikes = snapshot.getLong("NumberOfLikes") ?: 0
                        val newLikeCount = currentLikes + 1
                        transaction.update(
                            db.collection("Books").document(documentId),
                            "NumberOfLikes",
                            newLikeCount
                        )
                        val books = _uiBookState.value.firebaseBooks
                        val bookToUpdate = books.find { it.bookId == roomBook.id }
                        bookToUpdate?.let {
                            it.likes = newLikeCount.toInt()
                            _uiBookState.value = BookUiState(firebaseBooks = books)
                        }
                    }.await()

                    Log.d(
                        "MyViewModel",
                        "Successfully updated numberOfLikes for book: ${roomBook.id}"
                    )
                } else {
                    Log.e("MyViewModel", "No document found with bookId: ${roomBook.id}")
                }
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error updating numberOfLikes", e)
            }
        }
    }

    //whenever a user removes book to favourites it decrements the number of likes field in firebase
    fun decrementFavouriteCount(roomBook: RoomBook) {
        viewModelScope.launch {
            try {
                //Get the Firestore document ID using the bookId
                val querySnapshot = db.collection("Books")
                    .whereEqualTo("BookId", roomBook.id)
                    .get()
                    .await()

                //there's only one document with this bookId
                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    // Update the numberOfLikes field
                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(db.collection("Books").document(documentId))
                        val currentLikes = snapshot.getLong("NumberOfLikes") ?: 0
                        val newLikeCount = currentLikes - 1
                        transaction.update(
                            db.collection("Books").document(documentId),
                            "NumberOfLikes",
                            newLikeCount
                        )
                        val books = _uiBookState.value.firebaseBooks
                        val bookToUpdate = books.find { it.bookId == roomBook.id }
                        bookToUpdate?.let {
                            it.likes = newLikeCount.toInt()
                            _uiBookState.value = BookUiState(firebaseBooks = books)
                        }
                    }.await()

                    Log.d(
                        "MyViewModel",
                        "Successfully updated numberOfLikes for book: ${roomBook.id}"
                    )
                } else {
                    Log.e("MyViewModel", "No document found with bookId: ${roomBook.id}")
                }
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error updating numberOfLikes", e)
            }
        }
    }


    //REVIEWS
    // Function to fetch reviews for a specific book using the book's ID
    fun fetchReviews(bookId: String) {
        // Access the "Reviews" collection in the Firestore database
        db.collection("Reviews")
            // Filter the documents where the "bookId" field matches the provided bookId
            .whereEqualTo("bookId", bookId)
            // Get the filtered documents
            .get()
            // Add a success listener to handle the retrieved documents
            .addOnSuccessListener { result ->
                // Create a mutable list to store the reviews
                val reviews = mutableListOf<Review>()
                // Iterate over each document in the result
                for (document in result) {
                    // Retrieve the "email", "content", and "title" fields from the document
                    val email = document.getString("email") ?: ""
                    val reviewContent = document.getString("content") ?: ""
                    val title: String = document.getString("title") ?: ""
                    // Check if all retrieved fields are not blank
                    if (email.isNotBlank() && reviewContent.isNotBlank() && title.isNotBlank()) {
                        // Add a new Review object to the reviews list
                        reviews.add(Review(email, bookId, reviewContent, title))
                    }
                }
                // Update the UI state with the list of reviews
                _uiReviewState.value = ReviewUiState(reviews = reviews)
            }
            // Add a failure listener to handle any errors
            .addOnFailureListener { exception ->
                // Log the error message if documents could not be retrieved
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    //checks if a user has already reviewed a book and prevents them adding a new one
    fun hasUserReviewedBook(bookId: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isReviewed =
                currentUser?.email?.let { reviewRepository.hasUserReviewedBook(bookId) }
            if (isReviewed != null) {
                callback(isReviewed)
            }
        }
    }

     fun addRoomReview(bookId: String, content: String, title: String) {
        viewModelScope.launch {
            val roomReview = currentUser?.email?.let { RoomReview(bookId, content, title) }
            if (roomReview != null) {
                reviewRepository.insert(roomReview)
            }
        }
    }

    fun getReviewItem(id: String): LiveData<RoomReview> {
        return reviewRepository.getReviewItem(id)
    }

    fun getMyReviews() {
        viewModelScope.launch {
            reviewRepository.allReviews
                .asFlow()
                .collect { allReviews ->
                    _uiReviewState.update { currentState ->
                        currentState.copy(myReviews = allReviews)
                    }
                }
        }
    }

    // Function to add a review for a specific book adds to my review section and updates number of reviews count on firebase
    fun addReview(email: String, bookId: String, content: String, title: String) {
        // Create a Review object with the provided details
        val review = Review(email, bookId, content, title)

        // Add the review to the "Reviews" collection in the Firestore database
        db.collection("Reviews").add(review)
            .addOnSuccessListener { documentReference ->
                // Log a success message with the document ID if the review is added successfully
                Log.d("MyViewModel", "Review added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                // Log a warning message if there is an error adding the review
                Log.w("MyViewModel", "Error adding review", e)
            }

        // Launch a new coroutine within the ViewModel scope
        viewModelScope.launch {
            try {
                // Get the Firestore document ID using the bookId
                val querySnapshot = db.collection("Books")
                    .whereEqualTo("BookId", bookId)
                    .get()
                    .await()

                // Check if the query result is not empty
                if (querySnapshot.documents.isNotEmpty()) {
                    // Get the first document from the query result
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    // Run a Firestore transaction to update the number of reviews
                    db.runTransaction { transaction ->
                        // Get the current snapshot of the book document
                        val snapshot = transaction.get(db.collection("Books").document(documentId))
                        // Get the current number of reviews, defaulting to 0 if not present
                        val currentReviews = snapshot.getLong("NumberOfReviews") ?: 0
                        // Update the number of reviews in the book document
                        transaction.update(
                            db.collection("Books").document(documentId),
                            "NumberOfReviews",
                            currentReviews + 1
                        )
                        // Calculate the new review count
                        val newReviewCount = currentReviews + 1
                        // Update the local UI state with the new review count
                        val books = _uiBookState.value.firebaseBooks
                        val bookToUpdate = books.find { it.bookId == bookId }
                        bookToUpdate?.let {
                            it.numberOfReviews = newReviewCount.toInt()
                            _uiBookState.value = BookUiState(firebaseBooks = books)
                        }
                    }.await()
                    // Log a success message if the transaction is successful
                    Log.d("MyViewModel", "Successfully updated numberOfReviews for book: Book with id $bookId")
                } else {
                    // Log an error message if no document is found with the provided bookId
                    Log.e("MyViewModel", "No document found with bookId: $bookId")
                }
            } catch (e: Exception) {
                // Log an error message if there is an exception during the transaction
                Log.e("MyViewModel", "Error updating numberOfReviews", e)
            }
        }
    }

//removes a review from firebase and room when a user deletes it from my review section
    fun removeReview(bookId: String,  roomReview: RoomReview) {
        viewModelScope.launch {
            try {
                // Query to find the review document
                val querySnapshot = db.collection("Reviews")
                    .whereEqualTo("bookId", bookId)
                    .whereEqualTo("email", currentUser?.email)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    Log.d(TAG,document.id)
                    val documentId = document.id

                    // Delete the review document
                    db.collection("Reviews").document(documentId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("MyViewModel", "Review removed: $documentId")
                            // Decrement the number of reviews for the book
                            decrementNumberOfReviews(bookId)
                            // Remove the review from Room database
                            viewModelScope.launch {
                                reviewRepository.delete(roomReview)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("MyViewModel", "Error removing review: $documentId", e)
                        }
                } else {
                    if (currentUser != null) {
                        Log.e("MyViewModel", "No review found for bookId: $bookId and email: ${currentUser.email}")
                    }
                }
            } catch (e: Exception) {
                if (currentUser != null) {
                    Log.e("MyViewModel", "Error finding review for bookId: $bookId and email: ${currentUser.email}", e)
                }
            }
        }
    }

//retrieves a specific book and decrements its review count
    private fun decrementNumberOfReviews(bookId: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("Books")
                    .whereEqualTo("BookId", bookId)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    db.runTransaction { transaction ->
                        val snapshot = transaction.get(db.collection("Books").document(documentId))
                        val currentReviews = snapshot.getLong("NumberOfReviews") ?: 0
                        transaction.update(
                            db.collection("Books").document(documentId),
                            "NumberOfReviews",
                            currentReviews - 1 // Decrement the number of reviews
                        )
                        val newReviewCount = currentReviews + 1
                        val books = _uiBookState.value.firebaseBooks
                        val bookToUpdate = books.find { it.bookId == bookId }
                        bookToUpdate?.let {

                            it.numberOfReviews = newReviewCount.toInt()
                            _uiBookState.value = BookUiState(firebaseBooks = books)
                        }
                    }.await()
                    Log.d(
                        "MyViewModel",
                        "Successfully decremented numberOfReviews for book: $bookId"
                    )
                } else {
                    Log.e("MyViewModel", "No document found with bookId: $bookId")
                }
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error updating numberOfReviews", e)
            }
        }
    }

    fun editReview(reviewId: String, newTitle: String, newContent: String) {
        viewModelScope.launch {
            reviewRepository.updateReview(reviewId, newTitle, newContent)
        }
    }

    //when user in my reviews section can use a swipe event to edit a review this updates firebase
    fun updateReview(bookId: String, newContent: String, newEmail: String, newTitle: String) {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("Reviews")
                    .whereEqualTo("bookId", bookId)
                    .get()
                    .await()

                if (querySnapshot.documents.isNotEmpty()) {
                    val document = querySnapshot.documents[0]
                    val documentId = document.id

                    db.runTransaction { transaction ->
                        transaction.get(db.collection("Reviews").document(documentId))
                        transaction.update(
                            db.collection("Reviews").document(documentId),
                            mapOf(
                                "content" to newContent,
                                "email" to newEmail,
                                "title" to newTitle
                            )
                        )
                    }.await()

                    Log.d("MyViewModel", "Successfully updated review for book: $bookId")
                } else {
                    Log.e("MyViewModel", "No review found with bookId: $bookId")
                }
            } catch (e: Exception) {
                Log.e("MyViewModel", "Error updating review", e)
            }
        }
    }


    //READING LIST
    fun getMyReadingList() {
        viewModelScope.launch {
            readingListRepository.readingList
                .asFlow()
                .collect { readingList ->
                    _uiBookState.update { currentState ->
                        currentState.copy(readingList = readingList)
                    }
                }
        }
    }

    fun addToReadingList(readingListBook: ReadingListBook) {
        viewModelScope.launch {
            readingListRepository.insert(readingListBook)
        }
    }

    fun removeReadingList(readingListBook: ReadingListBook) {
        viewModelScope.launch {
            readingListRepository.delete(readingListBook)
        }
    }


    //USER
    fun insertUser() {
        viewModelScope.launch {
            val userId = currentUser?.uid ?: ""
            val user = currentUser?.email?.let { User(userId, it, isLoggedIn = true) }
            if (user != null) {
                userRepository.insertUser(user)
            }
        }
    }

    fun saveLoginState(isLoggedIn: Boolean) {
        sharedPreferencesManager.saveLoginState(isLoggedIn)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferencesManager.isLoggedIn()
    }

    fun clearUserData() {
        sharedPreferencesManager.saveLoginState(false)
    }

    //handles looking at the vibration haptics for the app when a book added to reading list
    fun isVibrationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED
    }

    fun requestVibrationPermission(launcher: ActivityResultLauncher<String>) {
        launcher.launch(Manifest.permission.VIBRATE)
    }

}