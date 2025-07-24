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
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.ui.unit.TextUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurant: Restaurant,
    onBackClick: () -> Unit,
    viewModel: RestaurantsViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var details by remember { mutableStateOf<PlaceDetailsResult?>(null) }
    val uriHandler = LocalUriHandler.current
    var visibleReviewsCount by remember { mutableStateOf(3) }

    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val displayName = firebaseUser?.displayName ?: ""

    var userComment by remember { mutableStateOf("") }
    var userRating by remember { mutableStateOf(0) }

    // טען פרטים של המסעדה מ-API כשמסעדה משתנה
    LaunchedEffect(restaurant.placeId) {
        details = getRestaurantDetails(restaurant.placeId)
    }

    // טען ביקורות מה-Firestore דרך ה-ViewModel כשמסעדה משתנה
    LaunchedEffect(restaurant.placeId) {
        viewModel.loadUserReviews(restaurant.placeId)
    }

    // רשימת ביקורות מה-Firestore (UserReview) שהפכנו ל-GoogleReview להצגה
    val firestoreReviews = viewModel.userReviews
    val googleReviews = details?.reviews ?: emptyList()

    val allReviews = firestoreReviews.map {
        GoogleReview(
            author_name = it.authorName,
            rating = it.rating,
            text = it.text
        )
    } + googleReviews

    Scaffold(
        topBar = {

            val isFavorite by remember { derivedStateOf { viewModel.isFavorite(restaurant.placeId) } }

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
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.toggleFavorite(restaurant.placeId)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else Color.Gray
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

            val phone = restaurant.phoneNumber ?: details?.formattedPhoneNumber
            phone?.let {
                InfoRow(
                    icon = Icons.Default.Phone,
                    label = it,
                    onClick = { uriHandler.openUri("tel:$it") }
                )
            }

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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp,
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {

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
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
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

                        openingHoursText.forEach { line ->
                            val parts = line.split(": ", limit = 2)
                            val dayPart = parts.getOrNull(0) ?: line
                            val hoursPart = parts.getOrNull(1) ?: ""

                            Row {
                                Text(
                                    text = "$dayPart: ",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = hoursPart,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
                                    color = Color.DarkGray
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // **שדות הוספת ביקורת מחוברים ל-ViewModel**
            AddReviewSection(
                userFullName = displayName,
                userComment = userComment,
                onUserCommentChange = { userComment = it },
                userRating = userRating,
                onUserRatingChange = { userRating = it },
                onSubmit = {
                    if (displayName.isNotBlank() && userComment.isNotBlank() && userRating > 0) {
                        val newReview = UserReview(
                            restaurantId = restaurant.placeId,
                            authorName = displayName,
                            rating = userRating,
                            text = userComment,
                            timestamp = System.currentTimeMillis()
                        )
                        viewModel.addUserReview(newReview) {
                            // ניקוי השדות אחרי שמירה מוצלחת
                            userComment = ""
                            userRating = 0
                        }
                    }
                },
                isSubmitEnabled = displayName.isNotBlank() && userComment.isNotBlank() && userRating > 0
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (allReviews.isNotEmpty()) {
                Text(
                    text = "Reviews",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = review.author_name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                                        color = Color.Black
                                    )

                                    if (review.author_name == displayName) {
                                        IconButton(onClick = {
                                            coroutineScope.launch {
                                                viewModel.deleteUserReview(
                                                    restaurantId = restaurant.id,
                                                    authorName = review.author_name,
                                                    text = review.text
                                                )
                                            }
                                        },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Review",
                                                tint = Color.Gray,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${review.rating}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    StarRating(rating = review.rating.toDouble())
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = review.text,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp),
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
                        enabled = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 8.dp, bottom = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFC7C7CC),
                            contentColor = Color(0xFF1C1C1E),
                            disabledContainerColor = Color(0xFFE5E5EA),
                            disabledContentColor = Color(0xFF1C1C1E).copy(alpha = 0.5f)
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Show More Reviews", fontSize = 15.sp)
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
    onClick: (() -> Unit)? = null,
    fontSize: TextUnit = 17.sp
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
fun AddReviewSection(
    userFullName: String,
    userComment: String,
    onUserCommentChange: (String) -> Unit,
    userRating: Int,
    onUserRatingChange: (Int) -> Unit,
    onSubmit: () -> Unit,
    isSubmitEnabled: Boolean
) {
    val background = Color.White
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF636366)
    val inputBackground = Color(0xFFF2F2F7)
    val buttonGray = Color(0xFFC7C7CC)
    val buttonTextColor = textPrimary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        shape = RoundedCornerShape(16.dp),
        color = background,
        shadowElevation = 4.dp  // <-- מוסיף הצללה
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Add Your Review",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                color = textPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your Name:",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = textSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userFullName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = textPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = userComment,
                onValueChange = onUserCommentChange,
                label = { Text("Your Comment", color = textSecondary, fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputBackground,
                    unfocusedContainerColor = inputBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = textPrimary,
                    focusedLabelColor = textSecondary,
                    unfocusedLabelColor = textSecondary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Your Rating:", fontSize = 15.sp, color = textSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                (1..5).forEach { star ->
                    Icon(
                        imageVector = if (userRating >= star) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onUserRatingChange(star) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSubmit,
                enabled = isSubmitEnabled,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonGray,
                    contentColor = buttonTextColor,
                    disabledContainerColor = Color(0xFFE5E5EA),
                    disabledContentColor = buttonTextColor.copy(alpha = 0.5f)
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text("Submit", fontSize = 15.sp)
            }
        }
    }
}
