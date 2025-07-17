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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border

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

@OptIn(ExperimentalMaterial3Api::class)
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
    val originalRestaurants = viewModel.mainOriginalRestaurants

    var sortOption by remember { mutableStateOf("none") }
    var ratingRange by remember { mutableStateOf(0f..5f) }
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

        SortButtonCompact(
            selectedOption = sortOption,
            onOptionSelected = { sortOption = it }
        )

        if (sortOption == "rating") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Rating range: ${"%.1f".format(ratingRange.start)} - ${"%.1f".format(ratingRange.endInclusive)}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )

                RangeSlider(
                    modifier = Modifier.height(30.dp),
                    value = ratingRange,
                    onValueChange = { ratingRange = it },
                    valueRange = 0f..5f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Gray,
                        inactiveTrackColor = Color.LightGray,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    ),
                    startThumb = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, Color.LightGray, CircleShape)
                                .background(Color.LightGray, CircleShape)
                        )
                    },
                    endThumb = {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(2.dp, Color.LightGray, CircleShape)
                                .background(Color.LightGray, CircleShape)
                        )
                    }
                )


            }
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
            var filteredRestaurants = if (searchResults.isNotEmpty()) searchResults else restaurants

            when (sortOption) {
                "open" -> {
                    filteredRestaurants = filteredRestaurants.filter { it.isOpenNow == true }
                }
                "name" -> {
                    filteredRestaurants = filteredRestaurants.sortedBy { it.name }
                }
            }

            if (sortOption == "rating") {
                filteredRestaurants = filteredRestaurants.filter {
                    it.rating >= ratingRange.start && it.rating <= ratingRange.endInclusive
                }
            }

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

@Composable
fun SortButtonCompact(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 6.dp)
            .wrapContentSize(Alignment.TopStart)
    ) {
        OutlinedButton(
            onClick = { expanded = true },
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(36.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = SolidColor(Color(0xFFBDBDBD)) // אפור בהיר
            )
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sort",
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when (selectedOption) {
                    "name" -> "Sort by name"
                    "open" -> "Open now only"
                    else -> "Sort"
                },
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White)
        ) {
            DropdownMenuItem(
                text = { Text("By name", color = Color.Black) },
                onClick = {
                    onOptionSelected("name")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Open now only", color = Color.Black) },
                onClick = {
                    onOptionSelected("open")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("By rating") },
                onClick = {
                    onOptionSelected("rating")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Clear sort", color = Color.Black) },
                onClick = {
                    onOptionSelected("none")
                    expanded = false
                }
            )
        }
    }
}
