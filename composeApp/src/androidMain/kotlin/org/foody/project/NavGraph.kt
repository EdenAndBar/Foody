package org.foody.project

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.gms.location.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import places.Restaurant
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Text


@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: RestaurantsViewModel = viewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // בקשת הרשאת מיקום
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                coroutineScope.launch {
                    val locString = getLastLocationString(context)
                    if (locString != null) {
                        viewModel.loadInitialRestaurants(locString)
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locString = getLastLocationString(context)
            if (locString != null) {
                viewModel.loadInitialRestaurants(locString)
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) "mainWrapper" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("mainWrapper") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("mainWrapper") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("mainWrapper") {
            MainScreen(
                navController = navController,
                viewModel = viewModel,
                onLogout = {
                    // מה לעשות כשמתבצע logout? למשל:
                    navController.navigate("login") {
                        popUpTo(0) // מנקה את הסטאק
                    }
                }
            )
        }


        composable("details/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")

            val restaurant = restaurantId?.let { id ->
                viewModel.mainApiResult.find { it.id == id }
                    ?: viewModel.locationSearchResults.find { it.id == id }
                    ?: viewModel.mainSearchResults.find { it.id == id }
            }

            if (restaurant != null) {
                RestaurantDetailScreen(
                    restaurant = restaurant,
                    onBackClick = { navController.popBackStack() }
                )
            } else {
                // אופציונלי: מסך fallback אם המסעדה לא נמצאה
                Text("Restaurant not found")
            }
        }


        composable("profile") {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }

        composable("about") {
            AboutUsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}

@SuppressLint("MissingPermission")
suspend fun getLastLocationString(context: android.content.Context): String? {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
    ) {
        return null
    }

    return suspendCancellableCoroutine { cont ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMaxUpdates(1)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location: Location? = result.lastLocation
                cont.resume(location?.let { "${it.latitude},${it.longitude}" }) {}
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
        fusedLocationClient.requestLocationUpdates(request, callback, null)
    }
}