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

    // COMMON
    var isLoading by mutableStateOf(false)
        private set

    var favorites by mutableStateOf<List<Restaurant>>(emptyList())
        private set

    private val api = RestaurantApiService()

    fun toggleFavorite(restaurant: Restaurant) {
        favorites = if (favorites.contains(restaurant)) {
            favorites - restaurant
        } else {
            favorites + restaurant
        }
    }
}
