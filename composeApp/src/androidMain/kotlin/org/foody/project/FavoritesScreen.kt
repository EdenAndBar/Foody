package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme

@Composable
fun FavoritesScreen(
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {
    val favorites by remember { derivedStateOf { viewModel.favorites } }

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("אין מועדפים עדיין", style = MaterialTheme.typography.titleMedium)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(favorites) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    isFavorite = true,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(restaurant)
                    },
                    onTap = {
                        onRestaurantClick(restaurant.id)
                    }
                )
            }
        }
    }
}
