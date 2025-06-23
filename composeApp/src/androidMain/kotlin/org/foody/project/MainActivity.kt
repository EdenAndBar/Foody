package org.foody.project

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import places.RestaurantApi
import places.Restaurant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            var apiResult by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
            val navController = rememberNavController()
            val coroutineScope = rememberCoroutineScope()

            val locationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted: Boolean ->
                    if (isGranted) {
                        // אם הרשאה ניתנה – משיגים מיקום
                        coroutineScope.launch {
                            val location = getLastLocation(context)
                            location?.let {
                                val locString = "${it.latitude},${it.longitude}"
                                val result = RestaurantApi.searchRestaurants(location = locString)
                                apiResult = result
                            }
                        }
                    } else {
                        println("⚠️ הרשאת מיקום לא ניתנה")
                    }
                }
            )

            LaunchedEffect(Unit) {
                when {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        coroutineScope.launch {
                            val location = getLastLocation(context)
                            location?.let {
                                val locString = "${it.latitude},${it.longitude}"
                                val result = RestaurantApi.searchRestaurants(location = locString)
                                apiResult = result
                            }
                        }
                    }
                    else -> {
                        // מבקשים הרשאה
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            }

            AppNavHost(navController = navController, restaurants = apiResult)
        }
    }

    private suspend fun getLastLocation(context: android.content.Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            println("⚠ אין הרשאת מיקום בעת קריאה ל־getLastLocation")
            return null
        }

        return kotlinx.coroutines.suspendCancellableCoroutine { cont ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location -> cont.resume(location) {} }
                    .addOnFailureListener { cont.resume(null) {} }
            } catch (e: SecurityException) {
                println("⚠ SecurityException בעת קבלת מיקום: ${e.message}")
                cont.resume(null) {}
            }
        }
    }

}
