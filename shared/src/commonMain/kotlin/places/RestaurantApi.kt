package org.foody.project

import kotlinx.coroutines.*
import places.Restaurant
import kotlin.coroutines.CoroutineContext
import places.PlaceDetailsResult
import kotlinx.coroutines.Dispatchers
import places.RestaurantApi
import places.getRestaurantDetails

class RestaurantApi : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    fun getRestaurants(location: String, callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = RestaurantApi.searchRestaurants(location = location)
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun getRestaurantsByCity(city: String, callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = RestaurantApi.searchRestaurants(city = city)
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }
//
//    fun getNearbyRestaurants(location: String, callback: (List<Restaurant>) -> Unit) {
//        launch {
//            try {
//                val results = RestaurantApi.searchRestaurants(location = location)
//                callback(results)
//            } catch (e: Exception) {
//                callback(emptyList())
//            }
//        }
//    }

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

}
