package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import places.Restaurant
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SwapVert


@Composable
fun SortButton(
    selectedSortOption: String,
    onSortSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
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
            )
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Sort",
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = when (selectedSortOption) {
                    "name" -> "Name (A-Z)"
                    "ratingAsc" -> "Rating ↑"
                    "ratingDesc" -> "Rating ↓"
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
                text = { Text("Name (A-Z)") },
                onClick = {
                    onSortSelected("name")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Rating ↑") },
                onClick = {
                    onSortSelected("ratingAsc")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Rating ↓") },
                onClick = {
                    onSortSelected("ratingDesc")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Clear sort") },
                onClick = {
                    onSortSelected("none")
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun FilterButton(
    isOpenNow: Boolean,
    onOpenNowToggle: () -> Unit,
    ratingRange: ClosedFloatingPointRange<Float>,
    onRatingRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onClearFilters: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
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
            )
        ) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = "Filter",
                tint = Color.DarkGray,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Filter", fontSize = 14.sp)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .padding(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Checkbox(
                    checked = isOpenNow,
                    onCheckedChange = { onOpenNowToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color.Gray,
                        uncheckedColor = Color.Gray,
                        checkmarkColor = Color.White
                    )
                )

                Text("Open now", fontSize = 14.sp)
            }

            RatingRange(
                ratingRange = ratingRange,
                onRatingRangeChange = onRatingRangeChange
            )

            Divider(modifier = Modifier.padding(vertical = 5.dp))

            TextButton(
                onClick = {
                    onClearFilters()
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear filters", color = Color.Black, fontSize = 14.sp)
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingRange(
    ratingRange: ClosedFloatingPointRange<Float>,
    onRatingRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
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
            onValueChange = onRatingRangeChange,
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

fun filterAndSortRestaurants(
    restaurants: List<Restaurant>,
    sortOption: String,
    isOpenNow: Boolean,
    ratingRange: ClosedFloatingPointRange<Float>
): List<Restaurant> {
    var filtered = restaurants

    if (isOpenNow) {
        filtered = filtered.filter { it.isOpenNow == true }
    }

    filtered = filtered.filter {
        it.rating >= ratingRange.start && it.rating <= ratingRange.endInclusive
    }

    filtered = when (sortOption) {
        "name" -> filtered.sortedBy { it.name }
        "ratingAsc" -> filtered.sortedBy { it.rating }
        "ratingDesc" -> filtered.sortedByDescending { it.rating }
        else -> filtered
    }

    return filtered
}
