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
import com.example.pinam.data.CircleItem
import com.example.pinam.data.PinItem
import com.example.pinam.data.SampleData
import com.example.pinam.ui.screens.*
import com.example.pinam.ui.theme.*
import com.example.pinam.viewmodel.AuthViewModel

sealed class NavItem(val route: String, val title: String, val icon: ImageVector) {
    object Circles : NavItem("circles", "Circles", Icons.Default.Group)
    object Map : NavItem("map", "Map", Icons.Default.Map)
    object Discover : NavItem("discover", "Discover", Icons.Default.CompassCalibration)
}

@Composable
fun PinamAppNavHost(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = remember { AuthViewModel() }
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val authState by authViewModel.uiState.collectAsState()

    // Dynamic State for Circles, Pins, and Dialogs
    val circlesList = remember { mutableStateListOf(*SampleData.sampleCircles.toTypedArray()) }
    var activeCircle by remember { mutableStateOf(circlesList.firstOrNull()) }
    var selectedPin by remember { mutableStateOf(SampleData.rooftopCafePin) }

    var showCreateSheet by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showLeaveCircleDialog by remember { mutableStateOf<CircleItem?>(null) }
    var showDeletePinDialog by remember { mutableStateOf(false) }

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
                startDestination = if (authState.currentUser != null) NavItem.Circles.route else "auth"
            ) {
                // Auth Screen
                composable("auth") {
                    AuthScreen(
                        viewModel = authViewModel,
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
                        circles = circlesList,
                        onCircleClick = { circle ->
                            activeCircle = circle
                            navController.navigate(NavItem.Map.route)
                        },
                        onCreateClick = { showCreateSheet = true },
                        onJoinClick = { showJoinDialog = true },
                        onLeaveCircle = { circle ->
                            showLeaveCircleDialog = circle
                        }
                    )
                }

                // Map Screen
                composable(NavItem.Map.route) {
                    MapScreen(
                        circleName = activeCircle?.name ?: "Osu Crew",
                        friendCountText = "${activeCircle?.memberAvatars?.size ?: 4} friends",
                        avatars = activeCircle?.memberAvatars ?: SampleData.sampleCircles[0].memberAvatars,
                        onAddPinClick = { navController.navigate("add_pin") },
                        onPinDetailClick = { pin ->
                            selectedPin = pin
                            navController.navigate("pin_detail")
                        }
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
                        pin = selectedPin,
                        onBack = { navController.popBackStack() },
                        onDeleteClick = { showDeletePinDialog = true },
                        onEditClick = { navController.navigate("add_pin") }
                    )
                }

                // Discover Screen
                composable(NavItem.Discover.route) {
                    DiscoverScreen(
                        onItemClick = { discoverItem ->
                            selectedPin = PinItem(
                                id = discoverItem.id,
                                placeName = discoverItem.placeName,
                                vibeTag = discoverItem.vibeTags.firstOrNull() ?: "Cozy",
                                authorAndDate = "Kofi · 2h ago",
                                caption = discoverItem.friendLovedText,
                                gradientBrush = discoverItem.gradientBrush
                            )
                            navController.navigate("pin_detail")
                        }
                    )
                }
            }

            // Create Circle Bottom Sheet
            if (showCreateSheet) {
                CreateCircleBottomSheet(
                    onDismiss = { showCreateSheet = false },
                    onCreateSuccess = { newName ->
                        val newCircle = CircleItem(
                            id = "c_${System.currentTimeMillis()}",
                            name = newName,
                            pinCountText = "0 pins",
                            gradientBrush = listOf(
                                GradientOrangeBrush,
                                GradientTealBrush,
                                GradientPinkBrush,
                                GradientPurpleBrush
                            ).random(),
                            memberAvatars = listOf(
                                "A" to AvatarOrange,
                                "K" to AvatarGreen
                            )
                        )
                        circlesList.add(0, newCircle)
                        activeCircle = newCircle
                        showCreateSheet = false
                        navController.navigate(NavItem.Map.route)
                    }
                )
            }

            // Join Circle Dialog
            if (showJoinDialog) {
                JoinCircleDialog(
                    onDismiss = { showJoinDialog = false },
                    onJoinSuccess = { code ->
                        val joinedCircle = CircleItem(
                            id = "c_joined_${System.currentTimeMillis()}",
                            name = "Joined Circle ($code)",
                            pinCountText = "5 pins",
                            gradientBrush = GradientTealBrush,
                            memberAvatars = listOf(
                                "A" to AvatarOrange,
                                "K" to AvatarGreen,
                                "E" to AvatarPink
                            )
                        )
                        circlesList.add(0, joinedCircle)
                        activeCircle = joinedCircle
                        showJoinDialog = false
                        navController.navigate(NavItem.Map.route)
                    }
                )
            }

            // Leave Circle Dialog
            showLeaveCircleDialog?.let { circleToLeave ->
                LeaveCircleDialog(
                    circleName = circleToLeave.name,
                    onDismiss = { showLeaveCircleDialog = null },
                    onConfirmLeave = {
                        circlesList.remove(circleToLeave)
                        if (activeCircle?.id == circleToLeave.id) {
                            activeCircle = circlesList.firstOrNull()
                        }
                        showLeaveCircleDialog = null
                    }
                )
            }

            // Delete Pin Dialog
            if (showDeletePinDialog) {
                DeletePinDialog(
                    onDismiss = { showDeletePinDialog = false },
                    onConfirmDelete = {
                        showDeletePinDialog = false
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
