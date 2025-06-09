package org.foody.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.*
// import kotlinx.coroutines.*
import places.searchRestaurants

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var apiResult by remember { mutableStateOf("Loading restaurants...") }

            LaunchedEffect(Unit) {
                val result = searchRestaurants()
                apiResult = result.take(500)
            }

            Text(text = apiResult)
        }
    }
}


//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}