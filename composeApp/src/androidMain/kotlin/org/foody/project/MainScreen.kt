package org.foody.project

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import places.Restaurant

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
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName ?: ""
    val firstName = displayName.split(" ").firstOrNull() ?: ""

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFFF9F9F9),
                modifier = Modifier.fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {
                        Text(
                            text = "Hello, $firstName!",
                            fontSize = 18.sp,
                            color = Color(0xFF1C1C1E)
                        )
                    }

                    Divider(color = Color.LightGray)

                    DrawerItem(icon = Icons.Default.Person, label = "Profile") {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("profile")
                    }

                    //Spacer(modifier = Modifier.height(8.dp))

                    DrawerItem(icon = Icons.Default.Info, label = "About Foody") {
                        coroutineScope.launch { drawerState.close() }
                        // TODO: Add settings screen
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Divider(color = Color.LightGray)

                    DrawerItem(icon = Icons.Default.ExitToApp, label = "Logout", color = Color.Red) {
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                    }
                }
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
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
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
                            onRestaurantClick = {
                                navController.navigate("details/${it.id}")
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

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    color: Color = Color(0xFF1C1C1E),
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = {
            Text(label, fontSize = 16.sp, color = color)
        },
        selected = false,
        onClick = onClick,
        icon = {
            Icon(imageVector = icon, contentDescription = label, tint = color)
        },
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
