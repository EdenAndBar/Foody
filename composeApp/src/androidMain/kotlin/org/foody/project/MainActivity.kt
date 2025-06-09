package org.foody.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import org.foody.project.RestaurantScreen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import places.searchRestaurants


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var apiResult by remember { mutableStateOf<List<String>>(emptyList()) }

            LaunchedEffect(Unit) {
                val result = searchRestaurants()
                apiResult = result.take(10)
            }

            RestaurantScreen(urls = apiResult)
        }

    }
}
