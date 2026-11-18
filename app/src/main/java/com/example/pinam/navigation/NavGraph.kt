package com.example.pinam.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pinam.ui.screens.*
import com.example.pinam.ui.theme.PrimaryPurple
import com.example.pinam.ui.theme.TextNearBlack
import com.example.pinam.ui.theme.TextSecondaryGray

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Circles : NavItem("circles", "Circles", Icons.Default.Group)
    object Map : NavItem("map", "Map", Icons.Default.Map)
    object Discover : NavItem("discover", "Discover", Icons.Default.CompassCalibration)
}

@Composable
fun PinamAppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showCreateSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val bottomNavItems = listOf(
        NavItem.Circles,
        NavItem.Map,
        NavItem.Discover
    )

    val isBottomBarVisible = currentRoute in listOf(
        NavItem.Circles.route,
        NavItem.Map.route,
        NavItem.Discover.route
    )

    Scaffold(
        bottomBar = {
            if (isBottomBarVisible) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = TextNearBlack
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(NavItem.Circles.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = PrimaryPurple,
                                selectedTextColor = PrimaryPurple,
                                indicatorColor = Color(0xFFF4F0FF)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = "auth"
            ) {
                // Auth Screen
                composable("auth") {
                    AuthScreen(
                        onAuthSuccess = {
                            navController.navigate(NavItem.Circles.route) {
                                popUpTo("auth") { inclusive = true }
                            }
                        }
                    )
                }

                // Circles Screen
                composable(NavItem.Circles.route) {
                    CirclesScreen(
                        onCircleClick = {
                            navController.navigate(NavItem.Map.route)
                        },
                        onCreateClick = { showCreateSheet = true },
                        onJoinClick = { showCreateSheet = true }
                    )
                }

                // Map Screen
                composable(NavItem.Map.route) {
                    MapScreen(
                        onAddPinClick = { navController.navigate("add_pin") },
                        onPinDetailClick = { navController.navigate("pin_detail") }
                    )
                }

                // Add Pin Screen
                composable("add_pin") {
                    AddPinScreen(
                        onBack = { navController.popBackStack() },
                        onDropPinSuccess = { navController.popBackStack() }
                    )
                }

                // Pin Detail Screen
                composable("pin_detail") {
                    PinDetailScreen(
                        onBack = { navController.popBackStack() },
                        onDeleteClick = { showDeleteDialog = true },
                        onEditClick = { navController.navigate("add_pin") }
                    )
                }

                // Discover Screen
                composable(NavItem.Discover.route) {
                    DiscoverScreen(
                        onItemClick = { navController.navigate("pin_detail") }
                    )
                }
            }

            // Create Circle Sheet
            if (showCreateSheet) {
                CreateCircleBottomSheet(
                    onDismiss = { showCreateSheet = false },
                    onCreateSuccess = {
                        showCreateSheet = false
                        navController.navigate(NavItem.Map.route)
                    }
                )
            }

            // Delete Pin Dialog
            if (showDeleteDialog) {
                DeletePinDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirmDelete = {
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
