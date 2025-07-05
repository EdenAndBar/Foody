package org.foody.project

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import places.Restaurant
import places.RestaurantApi

class RestaurantsViewModel : ViewModel() {

    var apiResult by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var originalRestaurants by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var searchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var favorites by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    private val api = RestaurantApiService()

    var locationSearchResults by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    var isLocationSearchActive by mutableStateOf(false)
        private set

    // טוען מסעדות לפי מיקום (קבל מיקום כ-string "latitude,longitude")
    fun loadInitialRestaurants(location: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val result = RestaurantApi.searchRestaurants(location)
                apiResult = result
                originalRestaurants = result
                searchResults = emptyList()
            } catch (e: Exception) {
                // טיפול בשגיאות אם צריך
                apiResult = emptyList()
                originalRestaurants = emptyList()
                searchResults = emptyList()
            }
            isLoading = false
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun searchRestaurants() {
        if (searchQuery.isNotBlank()) {
            isLoading = true
            viewModelScope.launch {
                api.getRestaurantsByName(searchQuery) { results ->
                    searchResults = results
                    apiResult = results // לעדכן גם כאן כדי לשמור עקביות
                    isLoading = false
                }
            }
        } else {
            clearSearch(originalRestaurants)
        }
    }

    fun clearSearch(originalList: List<Restaurant>) {
        searchQuery = ""
        searchResults = emptyList()
        apiResult = originalList
    }

    fun toggleFavorite(restaurant: Restaurant) {
        favorites = if (favorites.contains(restaurant)) {
            favorites - restaurant
        } else {
            favorites + restaurant
        }
    }

    // אפשר להוסיף פונקציה שתטען מסעדות לפי מיקום בצורה async
    fun loadRestaurantsByLocation(location: String) {
        loadInitialRestaurants(location)
    }

    fun updateSearchResults(updatedList: List<Restaurant>) {
        apiResult = updatedList
    }

    fun loadRestaurantsByCity(city: String) {
        if (city.isNotBlank()) {
            isLoading = true
            isLocationSearchActive = true
            viewModelScope.launch {
                api.getRestaurantsByCity(city) { results ->
                    locationSearchResults = results
                    isLoading = false
                }
            }
        } else {
            isLocationSearchActive = false
            locationSearchResults = emptyList()
        }
    }
}
