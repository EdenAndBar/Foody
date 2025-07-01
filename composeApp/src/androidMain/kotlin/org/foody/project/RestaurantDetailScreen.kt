package org.foody.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import places.*
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var details by remember { mutableStateOf<PlaceDetailsResult?>(null) }
    val uriHandler = LocalUriHandler.current
    var visibleReviewsCount by remember { mutableStateOf(3) }

    var userName by remember { mutableStateOf("") }
    var userComment by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) }
    val customReviews = remember { mutableStateListOf<GoogleReview>() }

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
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF0F0F3)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp)),
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                AsyncImage(
                    model = restaurant.photoUrl,
                    contentDescription = "Restaurant photo",
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            InfoRow(icon = Icons.Default.LocationOn, label = restaurant.address)

            details?.let { detail ->
                val websiteUrl = detail.website ?: detail.url
                websiteUrl?.let { url ->
                    InfoRow(
                        icon = Icons.Default.Language,
                        label = "Visit Website",
                        onClick = { uriHandler.openUri(url) }
                    )
                }
            }

            InfoRow(icon = Icons.Default.Star, label = "Rating: ${restaurant.rating}")

            Spacer(modifier = Modifier.height(11.dp))

            val currentDetails = details
            val openingHoursText = currentDetails?.opening_hours?.weekdayText
            val isOpenNow = currentDetails?.opening_hours?.open_now

            if (openingHoursText != null && openingHoursText.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF4A4A4A),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Opening Hours - ",
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        color = Color.Black
                    )
                    if (isOpenNow != null) {
                        Text(
                            text = if (isOpenNow) "OPEN NOW" else "CLOSED",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                            color = if (isOpenNow) Color(0xFF66BB6A) else Color(0xFFF44336),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        openingHoursText.forEach { line ->
                            val parts = line.split(": ", limit = 2)
                            val dayPart = parts.getOrNull(0) ?: line
                            val hoursPart = parts.getOrNull(1) ?: ""

                            Row {
                                Text(
                                    text = "$dayPart: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = hoursPart,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            // שדות הוספת ביקורת
            Text(
                text = "Add Your Review",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userName,
                onValueChange = { userName = it },
                label = { Text("Your Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = userComment,
                onValueChange = { userComment = it },
                label = { Text("Your Comment") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your Rating:")
                Spacer(modifier = Modifier.width(8.dp))
                (1..5).forEach { star ->
                    Icon(
                        imageVector = if (userRating >= star) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { userRating = star }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (userName.isNotBlank() && userComment.isNotBlank() && userRating > 0) {
                        val newReview = GoogleReview(
                            author_name = userName,
                            rating = userRating,
                            text = userComment
                        )
                        customReviews.add(0, newReview)
                        userName = ""
                        userComment = ""
                        userRating = 0
                    }
                },
                enabled = userName.isNotBlank() && userComment.isNotBlank() && userRating > 0,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Submit Review")
            }

            Spacer(modifier = Modifier.height(20.dp))

            val allReviews = customReviews + (details?.reviews ?: emptyList())

            if (allReviews.isNotEmpty()) {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                allReviews.take(visibleReviewsCount).forEach { review ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn()
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 2.dp,
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = review.author_name,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${review.rating}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    StarRating(rating = review.rating.toDouble())
                                }
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

                if (visibleReviewsCount < allReviews.size) {
                    Button(
                        onClick = {
                            visibleReviewsCount = minOf(visibleReviewsCount + 3, allReviews.size)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A4A4A),
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 8.dp)
                    ) {
                        Text(text = "show more reviews")
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
    val clickableColor = Color(0xFF4A4A4A)
    val defaultIconColor = Color(0xFF666666)
    val defaultTextColor = Color.Black

    val interactionSource = remember { MutableInteractionSource() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ) {
                onClick?.invoke()
            }
            .padding(vertical = 10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (onClick != null) clickableColor else defaultIconColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = if (onClick != null) clickableColor else defaultTextColor
        )
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
                modifier = Modifier.size(16.dp)
            )
        }

        if (hasHalfStar) {
            Icon(
                imageVector = Icons.Default.StarHalf,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }

        repeat(emptyStars) {
            Icon(
                imageVector = Icons.Default.StarBorder,
                contentDescription = null,
                tint = Color(0xFFFFD700),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
