package org.foody.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AboutUsScreen(
    onBackClick: () -> Unit
) {
    val backgroundColor = Color.White
    val textPrimary = Color(0xFF1C1C1E)
    val textSecondary = Color(0xFF636366)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // back arrow
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 20.dp, start = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = textPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 70.dp, start = 24.dp, end = 24.dp, bottom = 24.dp) // שים לב לרווח מלמעלה בגלל החץ
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.foody_logo),
                contentDescription = "Foody Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 5.dp)
            )

            Text(
                text = "Hungry?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Not sure what to eat or where to go?",
                fontSize = 16.sp,
                color = textSecondary
            )
            Text(
                text = "That’s exactly why we’re here!",
                fontSize = 16.sp,
                color = textSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Foody helps you discover restaurants around you, based on your location or a city you choose.",
                fontSize = 16.sp,
                color = textSecondary,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Feeling adventurous?",
                fontSize = 16.sp,
                color = textSecondary,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Check out our 🏆 Top 10 recommended spots!",
                fontSize = 16.sp,
                color = textSecondary,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Love a restaurant?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Add it to your favorites ❤️",
                fontSize = 16.sp,
                color = textSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Changed your mind?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Remove it with a tap 💔",
                fontSize = 16.sp,
                color = textSecondary
            )

        }
    }
}
