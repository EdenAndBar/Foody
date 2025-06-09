package org.foody.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import places.searchRestaurants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var apiResult by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }

            LaunchedEffect(Unit) {
                val result = searchRestaurants()
                apiResult = result.take(10)
            }

            MainScreen(restaurantUrls = apiResult) // <-- החלפנו את RestaurantScreen

        }
    }
}
