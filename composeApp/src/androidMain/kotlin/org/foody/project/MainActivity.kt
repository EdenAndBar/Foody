package org.foody.project

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import places.Restaurant
import places.RestaurantApi

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
                onResult = { isGranted ->
                    if (isGranted) {
                        coroutineScope.launch {
                            val location = getLastLocation(context)
                            if (location != null) {
                                val locString = "${location.latitude},${location.longitude}"
                                println("📍 Location obtained: $locString")
                                val result = RestaurantApi.searchRestaurants(location = locString)
                                println("🍽 API result: $result")
                                apiResult = result
                            } else {
                                println("⚠ לא התקבל מיקום")
                            }
                        }
                    } else {
                        println("⚠ הרשאת מיקום לא ניתנה")
                    }
                }
            )

            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    coroutineScope.launch {
                        val location = getLastLocation(context)
                        if (location != null) {
                            val locString = "${location.latitude},${location.longitude}"
                            println("📍 Location obtained: $locString")
                            val result = RestaurantApi.searchRestaurants(location = locString)
                            println("🍽 API result: $result")
                            apiResult = result
                        } else {
                            println("⚠ לא התקבל מיקום")
                        }
                    }
                } else {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            AppNavHost(
                navController = navController,
                restaurants = apiResult,
                onNewSearchResults = { updatedList ->
                    apiResult = updatedList
                }
            )

        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLastLocation(context: android.content.Context): Location? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            println("⚠ אין הרשאת מיקום בעת קריאה ל־getLastLocation")
            return null
        }

        return suspendCancellableCoroutine { cont ->
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMaxUpdates(1)
                .build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation
                    println("📌 onLocationResult: $location")
                    cont.resume(location) {}
                    fusedLocationClient.removeLocationUpdates(this)
                }

                override fun onLocationAvailability(availability: LocationAvailability) {
                    println("📌 onLocationAvailability: ${availability.isLocationAvailable}")
                }
            }

            fusedLocationClient.requestLocationUpdates(request, callback, null)
        }
    }
}
