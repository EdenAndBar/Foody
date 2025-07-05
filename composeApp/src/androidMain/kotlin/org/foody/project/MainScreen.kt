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
import android.annotation.SuppressLint
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource



sealed class BottomNavItem(val label: String, val icon: ImageVector) {
    object Main : BottomNavItem("Main", Icons.Default.Home)
    object Favorites : BottomNavItem("Favorites", Icons.Default.Favorite)
    object Location : BottomNavItem("Location", Icons.Default.LocationOn)
    object Category : BottomNavItem("Category", Icons.Default.Menu)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: RestaurantsViewModel,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Main) }
    val user = FirebaseAuth.getInstance().currentUser
    val displayName = user?.displayName ?: ""
    val firstName = displayName.split(" ").firstOrNull() ?: ""

    var currentLocation by remember { mutableStateOf<String?>(null) }

    // קריאה לקבלת מיקום והטענת מסעדות
    GetCurrentLocation { location ->
        if (currentLocation == null) { // כדי להפעיל פעם אחת בלבד
            currentLocation = location
            viewModel.loadInitialRestaurants(location)
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
                        coroutineScope.launch { drawerState.close() }
                        // TODO: Add settings screen here if needed
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
                                // לא צריך לעשות כאן כלום לגבי רשימות, ה-ViewModel מטפל בכך
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
                            navController = navController,
                            viewModel = viewModel,  // מעבירים את ה-ViewModel
                            onRestaurantClick = { navController.navigate("details/${it.id}") }
                        )
                    }
                    is BottomNavItem.Favorites -> {
                        Text(
                            "Favorites Screen",
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    is BottomNavItem.Location -> {
                        LocationScreen(
                            viewModel = viewModel,
                            onRestaurantClick = { navController.navigate("details/${it.id}?from=location") }
                        )
                    }
                    is BottomNavItem.Category -> {
                        Text(
                            "Filter by Category",
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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