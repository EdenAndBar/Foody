package org.foody.project

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import places.Restaurant

@Composable
fun AppNavHost(
    navController: NavHostController,
    restaurants: List<Restaurant>,
    onNewSearchResults: (List<Restaurant>) -> Unit,
    originalRestaurants: List<Restaurant>
) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            MainScreen(
                restaurants = restaurants,
                navController = navController,
                onNewSearchResults = onNewSearchResults,
                originalRestaurants = originalRestaurants
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
    }
}




