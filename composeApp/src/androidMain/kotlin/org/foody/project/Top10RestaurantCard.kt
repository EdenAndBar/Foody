package org.foody.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.StarHalf
import places.Restaurant

@Composable
fun Top10RestaurantCard(
    restaurant: Restaurant,
    index: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Restaurant Image
            AsyncImage(
                model = restaurant.photoUrl,
                contentDescription = "Restaurant Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.4f), Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Rank number (top-left)
            Text(
                text = "${index + 1}",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )

            // Restaurant name (centered)
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = restaurant.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp)) // רווח קטן
                StarRating(rating = restaurant.rating.toDouble())
            }

        }
    }
}

@Composable
fun StarRating(rating: Double, maxStars: Int = 5) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val fullStars = rating.toInt()
        val hasHalfStar = (rating - fullStars) >= 0.25 && (rating - fullStars) < 0.75
        val emptyStars = maxStars - fullStars - if (hasHalfStar) 1 else 0

        repeat(fullStars) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(18.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(18.dp)
            )
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

