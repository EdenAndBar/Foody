package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import places.Restaurant

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .height(52.dp)
            .background(Color(0xFFE0E0E5), RoundedCornerShape(12.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = {
                Text(
                    "Search restaurants...",
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.DarkGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true
        )
        IconButton(
            onClick = {
                keyboardController?.hide()
                onSearchSubmit()
            }
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.DarkGray
            )
        }
    }
}


@Composable
fun RestaurantScreen(
    restaurants: List<Restaurant>,
    onRestaurantClick: (Restaurant) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var favorites by remember { mutableStateOf(listOf<Restaurant>()) }
    var searchResults by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    val api = remember { org.foody.project.RestaurantApiService() }
    val coroutineScope = rememberCoroutineScope()

    fun performSearch() {
        if (searchQuery.isNotBlank()) {
            isLoading = true
            coroutineScope.launch {
                api.getRestaurantsByName(searchQuery) { results ->
                    searchResults = results
                    isLoading = false
                }
            }
        } else {
            searchResults = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchChanged = { searchQuery = it },
            onSearchSubmit = { performSearch() }
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                CircularProgressIndicator(color = Color(0xFF4A4A4A))
            }
        } else {
            val filteredRestaurants = if (searchResults.isNotEmpty()) searchResults else restaurants

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
                            onRestaurantClick(clickedRestaurant)
                        }
                    )
                }
            }
        }
    }
}
