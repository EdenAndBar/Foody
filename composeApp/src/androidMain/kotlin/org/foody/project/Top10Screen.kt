package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import places.Restaurant

@Composable
fun Top10Screen(
    restaurants: List<Restaurant>,
    isLoading: Boolean,
    favorites: List<Restaurant>,
    onRestaurantClick: (String) -> Unit,
    onFavoriteClick: (Restaurant) -> Unit,
    onRefreshClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // כותרת - מיושרת למרכז
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Top 10 Restaurants",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.Black
                )
            }

            // כפתור רענון - מיושר לימין
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, bottom = 4.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onRefreshClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF4A4A4A)
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4A4A4A))
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    itemsIndexed(restaurants) { index, restaurant ->
                        Top10RestaurantCard(
                            restaurant = restaurant,
                            index = index,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun Top10ScreenWrapper(
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit,
    onFavoriteClick: (Restaurant) -> Unit
) {
    val restaurants by remember { derivedStateOf { viewModel.top10Restaurants } }
    val isLoading by remember { derivedStateOf { viewModel.isLoadingTop10 } }

    LaunchedEffect(key1 = restaurants.isEmpty()) {
        if (restaurants.isEmpty()) {
            viewModel.loadTop10Restaurants()
        }
    }

    Top10Screen(
        restaurants = restaurants,
        isLoading = isLoading,
        favorites = viewModel.favorites,
        onRestaurantClick = onRestaurantClick,
        onFavoriteClick = onFavoriteClick,
        onRefreshClick = { viewModel.loadTop10Restaurants() }
    )
}
