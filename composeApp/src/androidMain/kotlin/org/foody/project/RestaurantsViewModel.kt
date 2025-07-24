package org.foody.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import android.util.Log
import places.Restaurant
import androidx.compose.runtime.State
import places.RestaurantApi
import places.UserReview

class RestaurantsViewModel : ViewModel() {

    private val restaurantMap = mutableStateMapOf<String, Restaurant>()
    val allRestaurants: List<Restaurant>
        get() = restaurantMap.values.toList()

    // --- MAIN TAB ---
    var mainSearchQuery by mutableStateOf("")
        private set

    var mainSearchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var mainApiResult by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    private val api = RestaurantApiService()

    fun loadInitialRestaurants(location: String) {
        isLoading = true
        api.getRestaurants(location) { results ->
            addOrUpdateRestaurants(results)
            mainApiResult = results
            mainSearchResults = emptyList()
            isLoading = false
        }
    }

    fun updateMainSearchQuery(query: String) {
        mainSearchQuery = query
    }

    fun searchRestaurants() {
        if (mainSearchQuery.isNotBlank()) {
            isLoading = true
            viewModelScope.launch {
                api.getRestaurantsByName(mainSearchQuery) { results ->
                    addOrUpdateRestaurants(results)
                    mainSearchResults = results
                    isLoading = false
                }
            }
        } else {
            clearMainSearch()
        }
    }

    fun clearMainSearch() {
        mainSearchQuery = ""
        mainSearchResults = emptyList()
    }

    // --- LOCATION TAB ---
    var locationSearchQuery by mutableStateOf("")
        private set

    var locationSearchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLocationSearchActive by mutableStateOf(false)
        private set

    var citySuggestions by mutableStateOf<List<String>>(emptyList())
        private set

    var shouldFetchSuggestions by mutableStateOf(true)
        private set

    var lastCitySearched by mutableStateOf<String?>(null)
        private set

    var hasSearchedCity by mutableStateOf(false)
        private set

    fun updateLocationSearchQuery(query: String) {
        locationSearchQuery = query
    }

    fun loadRestaurantsByCity(city: String) {
        val trimmedCity = city.trim().lowercase()
        if (trimmedCity.isNotBlank()) {
            isLoading = true
            isLocationSearchActive = true
            lastCitySearched = city
            viewModelScope.launch {
                api.getRestaurantsByCity(city) { results ->
                    val filtered = results.filter { it.address.lowercase().contains(trimmedCity) }
                    addOrUpdateRestaurants(filtered)
                    locationSearchResults = filtered
                    hasSearchedCity = true
                    isLoading = false
                }
            }
        } else {
            isLocationSearchActive = false
            locationSearchResults = emptyList()
            hasSearchedCity = false
        }
    }

    fun clearCitySearch() {
        isLocationSearchActive = false
        locationSearchResults = emptyList()
        lastCitySearched = null
        hasSearchedCity = false
        locationSearchQuery = ""
        clearCitySuggestions()
    }

    fun fetchCitySuggestions(query: String) {
        if (!shouldFetchSuggestions) return
        viewModelScope.launch {
            api.getCitySuggestions(query) { suggestions ->
                citySuggestions = suggestions
            }
        }
    }

    fun pauseSuggestionsFetching() {
        shouldFetchSuggestions = false
    }

    fun resumeSuggestionsFetching() {
        shouldFetchSuggestions = true
    }

    fun clearCitySuggestions() {
        citySuggestions = emptyList()
    }

    // --- FAVORITES TAB ---
    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val userId get() = auth.currentUser?.uid

    var shouldRefreshFavorites by mutableStateOf(false)
        private set

    fun markFavoritesDirty() {
        shouldRefreshFavorites = true
    }

    fun markFavoritesClean() {
        shouldRefreshFavorites = false
    }

    private val _favoritePlaceIds = mutableStateOf<Set<String>>(emptySet())
    val favoritePlaceIds: State<Set<String>> = _favoritePlaceIds

    val favorites: List<Restaurant>
        get() = allRestaurants.filter { favoritePlaceIds.value.contains(it.placeId) }

    fun toggleFavorite(placeId: String) {
        val current = _favoritePlaceIds.value.toMutableSet()
        if (current.contains(placeId)) {
            current.remove(placeId)
            removeFavoriteFromFirestore(placeId)
        } else {
            current.add(placeId)
            addFavoriteToFirestore(placeId)
        }
        _favoritePlaceIds.value = current
    }

    private fun addFavoriteToFirestore(placeId: String) {
        userId?.let { uid ->
            restaurantMap[placeId]?.let { restaurant ->
                val favRef = db.collection("users").document(uid)
                    .collection("favorites").document(placeId)
                favRef.set(restaurant)
            }
        }
    }

    private fun removeFavoriteFromFirestore(placeId: String) {
        userId?.let { uid ->
            val favRef = db.collection("users").document(uid)
                .collection("favorites").document(placeId)
            favRef.delete()
        }
    }

    fun loadFavorites() {
        userId?.let { uid ->
            isLoading = true
            db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener { result ->
                    val favs = result.mapNotNull { it.toObject(Restaurant::class.java) }
                    addOrUpdateRestaurants(favs) // מעדכן מסעדות במפה
                    _favoritePlaceIds.value = favs.map { it.placeId }.toSet()
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    fun isFavorite(placeId: String): Boolean {
        return favoritePlaceIds.value.contains(placeId)
    }


    // --- TOP 10 TAB ---
    var top10Restaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLoadingTop10 by mutableStateOf(false)
        private set

    fun loadTop10Restaurants() {
        isLoadingTop10 = true
        api.getTop10Restaurants { results ->
            addOrUpdateRestaurants(results)
            top10Restaurants = results
            isLoadingTop10 = false
        }
    }

    // --- COMMON ---
    var isLoading by mutableStateOf(false)
        private set

    // --- Sorting & Filtering ---
    var sortAlphabetically by mutableStateOf(false)
        private set

    var showOpenOnly by mutableStateOf(false)
        private set

    fun toggleSortAlphabetically() {
        sortAlphabetically = !sortAlphabetically
    }

    fun toggleShowOpenOnly() {
        showOpenOnly = !showOpenOnly
    }

    // --- USER REVIEWS ---
    var userReviews by mutableStateOf<List<UserReview>>(emptyList())
        private set

    fun loadUserReviews(restaurantId: String) {
        db.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { doc ->
                    val review = doc.toObject(UserReview::class.java)
                    review?.copy(id = doc.id)
                }
                userReviews = reviews
            }
            .addOnFailureListener { e ->
                Log.e("ViewModel", "❌ Failed to load reviews: $e")
            }
    }

    fun addUserReview(review: UserReview, onComplete: (() -> Unit)? = null) {
        db.collection("reviews")
            .add(review)
            .addOnSuccessListener {
                loadUserReviews(review.restaurantId)
                onComplete?.invoke()
            }
            .addOnFailureListener {
                onComplete?.invoke()
            }
    }

    fun deleteUserReview(restaurantId: String, authorName: String, text: String) {
        db.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .whereEqualTo("authorName", authorName)
            .whereEqualTo("text", text)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                }
                loadUserReviews(restaurantId)
            }
            .addOnFailureListener {
                Log.e("ViewModel", "❌ Failed to delete review: $it")
            }
    }

    // --- פונקציה מרכזית לעדכון/הוספה במפה ---
    private fun addOrUpdateRestaurants(restaurants: List<Restaurant>) {
        for (restaurant in restaurants) {
            restaurantMap[restaurant.placeId] = restaurant
        }
    }
}
