package org.foody.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.vector.ImageVector
import places.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var details by remember { mutableStateOf<PlaceDetailsResult?>(null) }
    val uriHandler = LocalUriHandler.current

    // שליפת פרטי המסעדה (לינק, ביקורות)
    LaunchedEffect(restaurant.placeId) {
        coroutineScope.launch {
            details = getRestaurantDetails(restaurant.placeId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F7)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = Color.White,
                tonalElevation = 8.dp
            ) {
                AsyncImage(
                    model = restaurant.photoUrl,
                    contentDescription = "Restaurant photo",
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // כתובת
            InfoRow(icon = Icons.Default.LocationOn, label = restaurant.address)
            // דירוג
            InfoRow(icon = Icons.Default.Star, label = "Rating: ${restaurant.rating}")

            val websiteUrl = details?.website ?: details?.url
            websiteUrl?.let { url ->
                InfoRow(
                    icon = Icons.Default.OpenInNew,
                    label = "Visit Website",
                    onClick = { uriHandler.openUri(url) }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // ביקורות (עד 3)
            details?.reviews?.let { reviews ->
                if (reviews.isNotEmpty()) {
                    Text(
                        text = "Reviews",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    reviews.take(3).forEach { review ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White,
                            tonalElevation = 2.dp
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "${review.author_name} - ${review.rating}⭐",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.text,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = if (onClick != null) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}
