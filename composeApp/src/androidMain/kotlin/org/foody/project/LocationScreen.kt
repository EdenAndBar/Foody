package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import places.Restaurant
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun LocationScreen(
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (Restaurant) -> Unit
) {
    val searchQuery = viewModel.searchQuery
    val isLoading = viewModel.isLoading
    val favorites = viewModel.favorites
    val isLocationSearchActive = viewModel.isLocationSearchActive
    val locationSearchResults = viewModel.locationSearchResults

    val citySuggestions = viewModel.citySuggestions
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchQuery) {
        val trimmed = searchQuery.trim()
        if (trimmed.isNotEmpty()) {
            viewModel.fetchCitySuggestions(trimmed)
        } else {
            viewModel.clearCitySuggestions()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.lastCitySearched?.let { lastCity ->
            if (lastCity.isNotBlank() && viewModel.locationSearchResults.isEmpty()) {
                viewModel.loadRestaurantsByCity(lastCity)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchChanged = { viewModel.updateSearchQuery(it) },
            onSearchSubmit = { viewModel.loadRestaurantsByCity(searchQuery) },
            onClearClick = {
                viewModel.clearSearch(emptyList())
                // נניח כאן גם ננקה את החיפוש של location:
                viewModel.loadRestaurantsByCity("") // כדי לנקות תוצאות
            }
        )

        if (citySuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    citySuggestions.forEach { suggestion ->
                        Column {
                            TextButton(
                                onClick = {
                                    viewModel.pauseSuggestionsFetching()
                                    viewModel.updateSearchQuery(suggestion)
                                    viewModel.clearCitySuggestions()
                                    viewModel.loadRestaurantsByCity(suggestion)

                                    coroutineScope.launch {
                                        delay(300)
                                        viewModel.resumeSuggestionsFetching()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = suggestion,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .fillMaxWidth(),
                                    color = Color.Black
                                )
                            }
                            Divider()
                        }
                    }
                }
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
            if (!isLocationSearchActive) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Please enter a city or address to search", color = Color.Gray)
                }
            } else if (locationSearchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No restaurants found.",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn {
                    items(locationSearchResults) { restaurant ->
                        RestaurantCard(
                            restaurant = restaurant,
                            isFavorite = favorites.contains(restaurant),
                            onFavoriteClick = { viewModel.toggleFavorite(it) },
                            onTap = { onRestaurantClick(it) }
                        )
                    }
                }
            }
        }
    }
}
