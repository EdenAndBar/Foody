package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
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

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFF2F2F7))
        .padding(16.dp)
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Top 10 Restaurants",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
            )
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(top10) { index, restaurant ->
                Top10RestaurantCard(
                    restaurant = restaurant,
                    index = index,
                    onClick = { onRestaurantClick(restaurant.id) }
                )
            }
        }
    }
}

