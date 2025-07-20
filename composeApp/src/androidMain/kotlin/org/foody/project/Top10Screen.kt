package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import places.Restaurant

@Composable
fun Top10Screen(
    restaurants: List<Restaurant>,
    favorites: List<Restaurant>,
    onRestaurantClick: (String) -> Unit,
    onFavoriteClick: (Restaurant) -> Unit
) {
    val top10 = restaurants
        .filter { it.rating >= 4.5 }                    // רק מסעדות עם דירוג 4.5 ומעלה
        .sortedByDescending { it.rating }               // למיין לפי דירוג
        .take(10)                                       // לקחת את 10 הראשונות

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Top 10 Restaurants",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(top10) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    isFavorite = favorites.any { it.id == restaurant.id },
                    onTap = { onRestaurantClick(restaurant.id) },
                    onFavoriteClick = { onFavoriteClick(restaurant) }
                )
            }
        }
    }
}
