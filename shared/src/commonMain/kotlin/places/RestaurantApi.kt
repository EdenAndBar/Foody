package org.foody.project

import kotlinx.coroutines.*
import places.Restaurant
import kotlin.coroutines.CoroutineContext
import places.searchRestaurants

class RestaurantApi : CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    fun testPrint() {
        println("📣 Kotlin function is visible in iOS!")
    }
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
}
