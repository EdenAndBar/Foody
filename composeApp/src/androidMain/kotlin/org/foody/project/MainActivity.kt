package org.foody.project

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import places.Restaurant
import places.RestaurantApi
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.location.Location
import kotlinx.coroutines.*
import android.content.pm.PackageManager
import android.os.Looper


suspend fun Task<Location>.awaitLocation(): Location? = suspendCancellableCoroutine { cont ->
    this.addOnSuccessListener { location ->
        cont.resume(location)
    }.addOnFailureListener { exception ->
        cont.resumeWithException(exception)
    }
}

class MainActivity : ComponentActivity() {

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)

        setContent {
            var apiResult by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
            val navController = rememberNavController()

            LaunchedEffect(Unit) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    println("❌ אין הרשאת מיקום")
                    return@LaunchedEffect
                }

                val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
                    priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 1000
                    numUpdates = 1
                }

                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : com.google.android.gms.location.LocationCallback() {
                        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                            val location = locationResult.lastLocation
                            if (location != null) {
                                println("✅ מיקום שהתקבל: ${location.latitude}, ${location.longitude}")
                                val latLng = "${location.latitude},${location.longitude}"

                                // כאן אנחנו בתוך callback אז נצטרך להפעיל coroutine בשביל לקרוא את ה-API
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val result = RestaurantApi.searchRestaurants(location = latLng)
                                        println("🔍 כמות מסעדות שהתקבלו: ${result.size}")

                                        withContext(Dispatchers.Main) {
                                            apiResult = result
                                        }
                                    } catch (e: Exception) {
                                        println("❌ שגיאה בקבלת מסעדות: ${e.localizedMessage}")
                                    }
                                }

                            } else {
                                println("⚠️ לא התקבל מיקום עדכני")
                            }
                            fusedLocationClient.removeLocationUpdates(this)
                        }
                    },
                    Looper.getMainLooper()
                )
            }



            AppNavHost(navController = navController, restaurants = apiResult)
        }
    }
}
