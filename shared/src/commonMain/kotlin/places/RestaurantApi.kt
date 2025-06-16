package org.foody.project

import kotlinx.coroutines.*
import places.Restaurant
import kotlin.coroutines.CoroutineContext
import places.searchRestaurants
import places.getRestaurantDetails as fetchRestaurantDetails
import places.PlaceDetailsResult
import kotlinx.coroutines.Dispatchers

class RestaurantApi : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    fun getRestaurants(callback: (List<Restaurant>) -> Unit) {
        launch {
            try {
                val results = searchRestaurants()
                callback(results)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }
    fun getRestaurantDetails(placeId: String, callback: (PlaceDetailsResult?) -> Unit) {
        launch {
            try {
                val result = places.getRestaurantDetails(placeId)
                callback(result)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

}
