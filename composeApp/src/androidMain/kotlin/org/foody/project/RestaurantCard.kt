package org.foody.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.background
import places.Restaurant

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    isFavorite: Boolean,
    onFavoriteClick: (String) -> Unit,
    onTap: (Restaurant) -> Unit,
    isOpenNow: Boolean? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { onTap(restaurant) },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = restaurant.photoUrl,
                    contentDescription = "Restaurant photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                restaurant.isOpenNow?.let { openNow ->
                    val labelColor = if (openNow) Color(0xFF4CAF50) else Color(0xFFF44336)
                    val labelText = if (openNow) "Open Now" else "Closed"

                    Text(
                        text = labelText,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(labelColor, shape = RoundedCornerShape(24.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = restaurant.address,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFD700)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", restaurant.rating),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.offset(y = 1.dp)
                    )
                    Spacer(modifier = Modifier.weight(5f))
                    IconButton(
                        onClick = { onFavoriteClick(restaurant.placeId) },
                        modifier = Modifier.size(25.dp).padding(end = 0.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}
