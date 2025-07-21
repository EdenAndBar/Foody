package org.foody.project

import kotlinx.coroutines.*
import places.Restaurant
import kotlin.coroutines.CoroutineContext
import places.PlaceDetailsResult
import kotlinx.coroutines.Dispatchers
import places.getRestaurantDetails
import places.RestaurantApi as PlacesApi


class RestaurantApiService : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    fun getRestaurants(location: String, callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = PlacesApi.searchRestaurants(location = location)
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun getRestaurantsByCity(city: String, callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = PlacesApi.searchRestaurants(city = city)
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun getRestaurantDetails(placeId: String, callback: (PlaceDetailsResult?) -> Unit) {
        launch {
            try {
                val result = getRestaurantDetails(placeId)
                callback(result)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    fun getRestaurantsByName(name: String, callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = PlacesApi.searchRestaurants(query = name)
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

     fun getCitySuggestions(query: String, callback: (List<String>) -> Unit) {
        launch {
            try {
                val suggestions = places.autocompleteCities(query)
                callback(suggestions)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun getTop10Restaurants(callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val all = PlacesApi.searchRestaurants(query = "")
                val highRated = all.filter { it.rating >= 4.0 }
                val remaining = all.filter { it.rating < 4.0 }

                val selected = mutableListOf<Restaurant>()
                selected.addAll(highRated.shuffled().take(10))

                if (selected.size < 10) {
                    val needed = 10 - selected.size
                    selected.addAll(remaining.shuffled().take(needed))
                }

                callback(selected.sortedByDescending { it.rating })
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }


}