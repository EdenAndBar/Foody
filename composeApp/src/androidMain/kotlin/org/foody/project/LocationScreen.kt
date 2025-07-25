package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun LocationScreen(
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {
    val searchQuery = viewModel.locationSearchQuery
    val isLoading = viewModel.isLoading
    val favorites = viewModel.favorites
    val isLocationSearchActive = viewModel.isLocationSearchActive
    val locationSearchResults = viewModel.locationSearchResults
    val citySuggestions = viewModel.citySuggestions
    val coroutineScope = rememberCoroutineScope()

    var sortOption by remember { mutableStateOf("none") }
    var ratingRange by remember { mutableStateOf(0f..5f) }
    var isOpenNow by remember { mutableStateOf(false) }

    //scroll to the top of the page
    val listState = rememberLazyListState()

    LaunchedEffect(searchQuery) {
        val trimmed = searchQuery.trim()
        if (!viewModel.hasSearchedCity && viewModel.shouldFetchSuggestions && trimmed.isNotEmpty()) {
            viewModel.fetchCitySuggestions(trimmed)
        } else {
            viewModel.clearCitySuggestions()
        }
    }

    // reload if there is no results
    LaunchedEffect(Unit) {
        viewModel.lastCitySearched?.let { lastCity ->
            if (lastCity.isNotBlank() && locationSearchResults.isEmpty()) {
                viewModel.loadRestaurantsByCity(lastCity)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        // Search bar by city
        SearchBar(
            searchQuery = searchQuery,
            onSearchChanged = { viewModel.updateLocationSearchQuery(it) },
            onSearchSubmit = {
                viewModel.loadRestaurantsByCity(searchQuery)
            },
            onClearClick = { viewModel.clearCitySearch() }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // sort and filter buttons on the left
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SortButton(
                    selectedSortOption = sortOption,
                    onSortSelected = { sortOption = it }
                )
                FilterButton(
                    isOpenNow = isOpenNow,
                    onOpenNowToggle = { isOpenNow = !isOpenNow },
                    ratingRange = ratingRange,
                    onRatingRangeChange = { ratingRange = it },
                    onClearFilters = {
                        isOpenNow = false
                        ratingRange = 0f..5f
                    }
                )
            }

            // refresh button on the right
            RefreshButton(
                onRefresh = {
                    viewModel.clearMainSearch()
                    sortOption = "none"
                    ratingRange = 0f..5f
                    isOpenNow = false
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }

        // City Suggestions
        if (citySuggestions.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            ) {
                Column(modifier = Modifier.background(Color.White)) {
                    citySuggestions.forEach { suggestion ->
                        Column {
                            TextButton(
                                onClick = {
                                    viewModel.pauseSuggestionsFetching()
                                    viewModel.updateLocationSearchQuery(suggestion)
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
        }

        else if (!isLocationSearchActive) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Please enter a city to search", color = Color.Gray)
            }
        }
        // no results
        else if (locationSearchResults.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No restaurants found.", color = Color.Gray, fontSize = 16.sp)
            }
        }
        // results
        else {
            val filteredRestaurants = filterAndSortRestaurants(
                locationSearchResults,
                sortOption,
                isOpenNow,
                ratingRange
            )

            LazyColumn(state = listState) {
                items(filteredRestaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        isFavorite = viewModel.isFavorite(restaurant.placeId),
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        onTap = { onRestaurantClick(restaurant.placeId) }
                    )
                }
            }
        }
    }
}
