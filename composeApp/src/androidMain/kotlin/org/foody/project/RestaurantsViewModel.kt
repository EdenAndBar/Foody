package org.foody.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query
import places.Restaurant
import places.RestaurantApi
import places.*

class RestaurantsViewModel : ViewModel() {

    // MAIN TAB
    var mainApiResult by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var mainOriginalRestaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var mainSearchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var mainSearchQuery by mutableStateOf("")
        private set

    fun loadInitialRestaurants(location: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RestaurantApi.searchRestaurants(location)
                mainApiResult = result
                mainOriginalRestaurants = result
                mainSearchResults = emptyList()
            } catch (e: Exception) {
                mainApiResult = emptyList()
                mainOriginalRestaurants = emptyList()
                mainSearchResults = emptyList()
            }
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

    // LOCATION TAB
    var locationSearchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLocationSearchActive by mutableStateOf(false)
        private set

    var locationSearchQuery by mutableStateOf("")
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
                    val filtered = results.filter {
                        it.address.lowercase().contains(trimmedCity)
                    }
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

    // FAVORITES TAB

    var favorites by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    private val auth = FirebaseAuth.getInstance()
    private val db = Firebase.firestore
    private val userId get() = auth.currentUser?.uid

    private var _shouldRefreshFavorites by mutableStateOf(true)
    val shouldRefreshFavorites: Boolean
        get() = _shouldRefreshFavorites

    fun markFavoritesDirty() {
        _shouldRefreshFavorites = true
    }

    fun markFavoritesClean() {
        _shouldRefreshFavorites = false
    }

    fun toggleFavorite(restaurant: Restaurant) {
        userId?.let { uid ->
            val favRef = db.collection("users").document(uid)
                .collection("favorites").document(restaurant.id)

            if (favorites.any { it.id == restaurant.id }) {
                favRef.delete()
                favorites = favorites.filterNot { it.id == restaurant.id }
            } else {
                favRef.set(restaurant)
                favorites = favorites + restaurant
            }

            markFavoritesDirty()
        }
    }


    fun loadFavorites() {
        userId?.let { uid ->
            isLoading = true
            db.collection("users").document(uid).collection("favorites")
                .get()
                .addOnSuccessListener { result ->
                    favorites = result.mapNotNull { it.toObject(Restaurant::class.java) }
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }


    fun isFavorite(restaurantId: String): Boolean {
        return favorites.any { it.id == restaurantId }
    }

    // TOP 10 TAB
    var top10Restaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLoadingTop10 by mutableStateOf(false)
        private set

    fun loadTop10Restaurants() {
        isLoadingTop10 = true
        api.getTop10Restaurants { results ->
            top10Restaurants = results
            isLoadingTop10 = false
        }
    }

    // COMMON
    var isLoading by mutableStateOf(false)
        private set

    private val api = RestaurantApiService()

    // Sorting & Filtering
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

    // USER REVIEW

    var userReviews by mutableStateOf<List<UserReview>>(emptyList())
        private set

    fun loadUserReviews(restaurantId: String) {
        Firebase.firestore.collection("reviews")
            .whereEqualTo("restaurantId", restaurantId)
            .orderBy("timestamp", Query.Direction.DESCENDING) // ⬅️ חדש: לפי זמן, מהחדש לישן
            .get()
            .addOnSuccessListener { snapshot ->
                val reviews = snapshot.documents.mapNotNull { it.toObject(UserReview::class.java) }
                println("🔄 Loaded ${reviews.size} reviews for $restaurantId")
                reviews.forEach {
                    println("📝 ${it.authorName} (${it.timestamp}): ${it.text}")
                }
                userReviews = reviews
            }
            .addOnFailureListener { e ->
                println("❌ Failed to load reviews: $e")
            }
    }

    fun addUserReview(review: UserReview, onComplete: (() -> Unit)? = null) {
        val db = Firebase.firestore
        db.collection("reviews")
            .add(review)
            .addOnSuccessListener {
                // עדכן את הרשימה מקומית
                userReviews = listOf(review) + userReviews
                onComplete?.invoke()
            }
            .addOnFailureListener {
                onComplete?.invoke()
            }
    }


}
