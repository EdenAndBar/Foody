package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import places.Restaurant

@Composable
fun SearchBar(searchQuery: String, onSearchChanged: (String) -> Unit) {
    TextField(
        value = searchQuery,
        onValueChange = onSearchChanged,
        placeholder = {
            Text(
                "Search restaurants...",
                color = Color.Black.copy(alpha = 0.5f),
                fontSize = MaterialTheme.typography.bodyMedium.fontSize // Placeholder קטן
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE0E0E5),
            unfocusedContainerColor = Color(0xFFE0E0E5),
            disabledContainerColor = Color(0xFFE0E0E5),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = Color.DarkGray,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        singleLine = true
    )
}

@Composable
fun RestaurantScreen(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Restaurant) -> Unit // הוספת פרמטר ניווט
) {
    var searchQuery by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(listOf<Restaurant>()) }

    val filteredRestaurants = restaurants.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        SearchBar(searchQuery = searchQuery, onSearchChanged = { searchQuery = it })

        LazyColumn {
            items(filteredRestaurants) { restaurant ->
                RestaurantCard(
                    restaurant = restaurant,
                    isFavorite = favorites.contains(restaurant),
                    onFavoriteClick = { clickedRestaurant ->
                        favorites = if (favorites.contains(clickedRestaurant)) {
                            favorites - clickedRestaurant
                        } else {
                            favorites + clickedRestaurant
                        }
                    },
                    onTap = { clickedRestaurant ->
                        onRestaurantClick(clickedRestaurant) // מעביר את הניווט החוצה
                    }
                )
            }
        }
    }
}


