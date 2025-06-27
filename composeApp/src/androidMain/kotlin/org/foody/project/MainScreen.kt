package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import places.Restaurant
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.sp




sealed class BottomNavItem(val label: String, val icon: ImageVector) {
    object Main : BottomNavItem("Main", Icons.Default.Home)
    object Favorites : BottomNavItem("Favorites", Icons.Default.Favorite)
    object Location : BottomNavItem("Location", Icons.Default.LocationOn)
    object Category : BottomNavItem("Category", Icons.Default.Menu)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    restaurants: List<Restaurant>,
    navController: NavHostController,
    onNewSearchResults: (List<Restaurant>) -> Unit,
    originalRestaurants: List<Restaurant>,
    onLogout: () -> Unit // 👈 נוספה פונקציה חדשה
) {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Main) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Foody", fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout() // 👈 נקרא כאשר המשתמש התנתק
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                listOf(
                    BottomNavItem.Main,
                    BottomNavItem.Favorites,
                    BottomNavItem.Location,
                    BottomNavItem.Category
                ).forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == item,
                        onClick = {
                            selectedItem = item
                            if (item == BottomNavItem.Main) {
                                onNewSearchResults(originalRestaurants)
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                is BottomNavItem.Main -> {
                    RestaurantScreen(
                        restaurants = restaurants,
                        navController = navController,
                        onRestaurantClick = { clickedRestaurant ->
                            navController.navigate("details/${clickedRestaurant.id}")
                        },
                        onNewSearchResults = onNewSearchResults,
                        originalRestaurants = originalRestaurants
                    )
                }
                is BottomNavItem.Favorites -> {
                    Text("Favorites Screen", modifier = Modifier.fillMaxSize())
                }
                is BottomNavItem.Location -> {
                    Text("Filter by Location", modifier = Modifier.fillMaxSize())
                }
                is BottomNavItem.Category -> {
                    Text("Filter by Category", modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
