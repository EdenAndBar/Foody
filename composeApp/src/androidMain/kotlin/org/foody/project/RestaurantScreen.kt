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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onSearchSubmit: () -> Unit,
    onClearClick: () -> Unit
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
        // כפתור ניקוי (הופיע רק אם יש טקסט)
        if (searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                onClearClick()
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                    tint = Color.DarkGray,
                    modifier = Modifier.size(21.dp)
                )
            }
        } else {
            Spacer(modifier = Modifier.width(8.dp)) // ריווח כשהכפתור לא מופיע
        }

        TextField(
            value = searchQuery,
            onValueChange = onSearchChanged,
            placeholder = {
                Text(
                    "Search restaurants...",
                    color = Color.Black.copy(alpha = 0.5f),
                    fontSize = 15.sp
                )
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.DarkGray,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
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
    navController: NavHostController,
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {

    val restaurants = viewModel.apiResult
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val searchResults = viewModel.searchResults
    val favorites = viewModel.favorites
    val originalRestaurants = viewModel.originalRestaurants

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchChanged = { viewModel.updateSearchQuery(it) },
            onSearchSubmit = { viewModel.searchRestaurants() },
            onClearClick = { viewModel.clearSearch(originalRestaurants) }
        )

        if (searchResults.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearSearch(originalRestaurants) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black,
                        modifier = Modifier.size(21.dp)
                    )
                }
                Spacer(modifier = Modifier.width(1.dp))
                Text(text = "Back", fontSize = 16.sp)
            }
        }

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
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        onTap = { onRestaurantClick(restaurant.id) }
                    )
                }
            }
        }
    }
}
