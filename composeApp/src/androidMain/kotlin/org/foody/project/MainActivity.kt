package org.foody.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.*
// import kotlinx.coroutines.*
import places.searchRestaurants
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var apiResult by remember { mutableStateOf<List<String>>(emptyList()) }

            LaunchedEffect(Unit) {
                val result = searchRestaurants()
                apiResult = result.take(10) // או כמה שרוצים להציג
            }

            LazyColumn {
                items(apiResult) { name ->
                    Text(text = name)
                }
            }
        }


//        setContent {
//            var apiResult by remember { mutableStateOf("Loading restaurants...") }
//
//            LaunchedEffect(Unit) {
//                val result = searchRestaurants()
//                apiResult = result.take(500)
//            }
//
//            Text(text = apiResult)
//        }
    }
}


//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}