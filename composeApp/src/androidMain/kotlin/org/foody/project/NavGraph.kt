package org.foody.project

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import places.Restaurant
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(
    navController: NavHostController,
    restaurants: List<Restaurant>,
    onNewSearchResults: (List<Restaurant>) -> Unit,
    originalRestaurants: List<Restaurant>
) {
    // בדיקה אם המשתמש מחובר
    val isUserLoggedIn = FirebaseAuth.getInstance().currentUser != null

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "list" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("list") {
                        popUpTo("login") { inclusive = true } // מסיר את מסך ההתחברות מהסטאק
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
                    navController.navigate("list") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("list") {
            MainScreen(
                restaurants = restaurants,
                navController = navController,
                onNewSearchResults = onNewSearchResults,
                originalRestaurants = originalRestaurants,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("list") { inclusive = true } // מוחק את היסטוריית הניווט
                    }
                }
            )
        }


        composable("details/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")
            val restaurant = restaurants.find { it.id == restaurantId }
            restaurant?.let {
                RestaurantDetailScreen(
                    restaurant = it,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable("profile") {
            ProfileScreen()
        }

    }
}
