package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import places.Restaurant

@Composable
fun FavoritesScreen(
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {
    val favorites by remember { derivedStateOf { viewModel.favorites } }
    val isLoading by remember { derivedStateOf { viewModel.isLoading } }

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
            .padding(16.dp)
    ) {
        Text(
            text = "Your Favorites",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.Gray)
                }
            }

            favorites.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorites yet",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(favorites) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            isFavorite = true,
                            onFavoriteClick = {
                                viewModel.toggleFavorite(restaurant.placeId)
                            },
                            onTap = {
                                onRestaurantClick(restaurant.placeId)
                            }
                        )
                    }
                }
            }
        }
    }
}
