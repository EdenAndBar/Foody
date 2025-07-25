package org.foody.project

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RefreshButton(
    onRefresh: () -> Unit
) {
    OutlinedButton(
        onClick = onRefresh,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.size(36.dp, 36.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black
        )
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Refresh",
            tint = Color.DarkGray,
            modifier = Modifier.size(18.dp)
        )
    }
}
