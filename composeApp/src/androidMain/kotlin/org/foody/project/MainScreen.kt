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
import android.annotation.SuppressLint
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.saveable.rememberSaveable

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Main : BottomNavItem("main", "Main", Icons.Default.Home)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Default.Favorite)
    object Location : BottomNavItem("location", "Location", Icons.Default.LocationOn)
    object Top10 : BottomNavItem("top10", "Top 10", Icons.Default.Star)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: RestaurantsViewModel,
    onLogout: () -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val bottomNavController = rememberNavController()

    val user = FirebaseAuth.getInstance().currentUser
    var displayName = user?.displayName ?: ""

    LaunchedEffect(Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reload()?.addOnCompleteListener {
            displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
        }
    }

    val firstName = displayName.split(" ").firstOrNull() ?: ""

    var currentLocation by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(currentLocation) {
        if (currentLocation != null && viewModel.mainApiResult.isEmpty()) {
            viewModel.loadInitialRestaurants(currentLocation!!)
        }
    }

    GetCurrentLocation { location ->
        if (currentLocation == null) {
            currentLocation = location
        }
    }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Hello, $firstName!",
                            fontSize = 18.sp,
                            color = Color(0xFF1C1C1E)
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Drawer",
                                modifier = Modifier.size(20.dp),
                                tint = Color(0xFF1C1C1E)
                            )
                        }
                    }

                    Divider(color = Color.LightGray)

                    DrawerItem(icon = Icons.Default.Person, label = "Profile") {
                        coroutineScope.launch { drawerState.close() }
                        navController.navigate("profile")
                    }

                    DrawerItem(icon = Icons.Default.Info, label = "About Foody") {
                        coroutineScope.launch {
                            drawerState.close()
                            navController.navigate("about")
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Divider(color = Color.LightGray)

                    DrawerItem(
                        icon = Icons.Default.ExitToApp,
                        label = "Logout",
                        color = Color(0xFFCE0E31)
                    ) {
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
                BottomNavigationBar(navController = bottomNavController)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                NavHost(
                    navController = bottomNavController,
                    startDestination = BottomNavItem.Main.route
                ) {
                    composable(BottomNavItem.Main.route){
                        RestaurantScreen(
                            navController = navController,
                            viewModel = viewModel,
                            onRestaurantClick = { id -> navController.navigate("details/$id") }
                        )
                    }
                    composable(BottomNavItem.Favorites.route) {
                        // החלף ל־FavoritesScreen אמיתי אם יש לך, כרגע טקסט לדוגמה
                        Box(
                            Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Favorites Screen",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    composable(BottomNavItem.Location.route) {
                        LocationScreen(
                            viewModel = viewModel,
                            onRestaurantClick = { id -> navController.navigate("details/$id") }
                        )
                    }
                    composable(BottomNavItem.Top10.route) {
                        Top10Screen(
                            restaurants = viewModel.getTop10Restaurants(),
                            favorites = viewModel.favorites,
                            onRestaurantClick = { id -> navController.navigate("details/$id") },
                            onFavoriteClick = { restaurant -> viewModel.toggleFavorite(restaurant) }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Main,
        BottomNavItem.Favorites,
        BottomNavItem.Location,
        BottomNavItem.Top10
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color(0xFF1C1C1E)
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (currentRoute == item.route) Color(0xFF1C1C1E) else Color(0xFF8E8E93)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 12.sp,
                        color = if (currentRoute == item.route) Color(0xFF1C1C1E) else Color(0xFF8E8E93)
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // שמירת מצב ומניעת איפוס
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFFF2F2F7)
                )
            )
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

@SuppressLint("MissingPermission")
@Composable
fun GetCurrentLocation(onLocationReceived: (String) -> Unit) {
    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    LaunchedEffect(Unit) {
        val cancellationTokenSource = CancellationTokenSource()
        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            location?.let {
                val locationStr = "${it.latitude},${it.longitude}"
                onLocationReceived(locationStr)
            }
        }
    }
}