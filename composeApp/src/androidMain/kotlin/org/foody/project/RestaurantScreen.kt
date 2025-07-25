package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.lazy.rememberLazyListState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope


@Composable
fun RestaurantScreen(
    navController: NavHostController,
    viewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {
    val restaurants = viewModel.mainSearchResults.ifEmpty { viewModel.mainApiResult }
    val searchQuery = viewModel.mainSearchQuery
    val isLoading = viewModel.isLoading
    val searchResults = viewModel.mainSearchResults
    val favorites = viewModel.favorites
    val originalRestaurants = viewModel.mainApiResult

    var sortOption by remember { mutableStateOf("none") }
    var ratingRange by remember { mutableStateOf(0f..5f) }
    var isOpenNow by remember { mutableStateOf(false) }

    //scroll to the top of the page
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F7))
    ) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchChanged = { viewModel.updateMainSearchQuery(it) },
            onSearchSubmit = { viewModel.searchRestaurants() },
            onClearClick = { viewModel.clearMainSearch() }
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

        if (searchResults.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearMainSearch() }
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
            val baseList = if (searchResults.isNotEmpty()) searchResults else restaurants
            val filteredRestaurants = filterAndSortRestaurants(baseList, sortOption, isOpenNow, ratingRange)

            LazyColumn(state = listState){
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
