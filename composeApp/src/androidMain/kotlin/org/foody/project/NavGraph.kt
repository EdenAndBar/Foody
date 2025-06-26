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
    onNewSearchResults: (List<Restaurant>) -> Unit
) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            MainScreen(
                restaurants = restaurants,
                navController = navController,
                onNewSearchResults = onNewSearchResults
            )
        }
        composable("details/{restaurantId}") { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId")
            val allRestaurants = restaurants // או מאיפה שאת שומרת את הרשימה
            val restaurant = allRestaurants.find { it.id == restaurantId }
            restaurant?.let {
                RestaurantDetailScreen(
                    restaurant = it,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}



