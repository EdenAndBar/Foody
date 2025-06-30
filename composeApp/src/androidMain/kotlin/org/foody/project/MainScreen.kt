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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color



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
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Main) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
                Text("Menu", modifier = Modifier.padding(16.dp), fontSize = 18.sp)
                Divider()

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("profile")
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        // הגדרות
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    actions = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF2F2F7)
                    ),
                    modifier = Modifier.height(55.dp)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF1C1C1E)
                ) {
                    listOf(
                        BottomNavItem.Main,
                        BottomNavItem.Favorites,
                        BottomNavItem.Location,
                        BottomNavItem.Category
                    ).forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    tint = if (selectedItem == item) Color(0xFF1C1C1E) else Color(0xFF8E8E93)
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 12.sp,
                                    color = if (selectedItem == item) Color(0xFF1C1C1E) else Color(0xFF8E8E93)
                                )
                            },
                            selected = selectedItem == item,
                            onClick = {
                                selectedItem = item
                                if (item == BottomNavItem.Main) {
                                    onNewSearchResults(originalRestaurants)
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFFF2F2F7)
                            )
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
}
