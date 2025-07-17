package org.foody.project

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import places.Restaurant

@Composable
fun SortButton(
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
    ratingRange: ClosedFloatingPointRange<Float>
): List<Restaurant> {
    var filtered = restaurants

    when (sortOption) {
        "open" -> {
            filtered = filtered.filter { it.isOpenNow == true }
        }
        "name" -> {
            filtered = filtered.sortedBy { it.name }
        }
    }

    if (sortOption == "rating") {
        filtered = filtered.filter { it.rating >= ratingRange.start && it.rating <= ratingRange.endInclusive }
    }

    return filtered
}
