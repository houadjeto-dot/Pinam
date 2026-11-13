package com.example.pinam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pinam.data.repository.LocalStoreManager
import com.example.pinam.ui.components.DropPinFab
import com.example.pinam.ui.components.PinamBottomNavBar
import com.example.pinam.ui.navigation.NavRoute
import com.example.pinam.ui.screens.auth.LoginSignUpScreen
import com.example.pinam.ui.screens.circles.CircleDetailsScreen
import com.example.pinam.ui.screens.circles.CirclesListScreen
import com.example.pinam.ui.screens.map.CircleMapScreen
import com.example.pinam.ui.screens.pin.DropPinScreen
import com.example.pinam.ui.screens.pin.PinDetailScreen
import com.example.pinam.ui.screens.recommendations.RecommendationsScreen
import com.example.pinam.ui.theme.PinamTheme
import com.example.pinam.viewmodel.AuthViewModel
import com.example.pinam.viewmodel.CircleViewModel
import com.example.pinam.viewmodel.PinViewModel
import com.example.pinam.viewmodel.RecommendationViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val circleViewModel: CircleViewModel by viewModels()
    private val pinViewModel: PinViewModel by viewModels()
    private val recommendationViewModel: RecommendationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalStoreManager.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            PinamTheme {
                val navController = rememberNavController()
                val authState by authViewModel.uiState.collectAsState()
                val circleState by circleViewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()

                val currentUserId = authState.currentUser?.id ?: ""
                val currentUserName = authState.currentUser?.displayName.orEmpty().ifBlank { "Friend" }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val isMainBottomTab = currentRoute in listOf(
                    NavRoute.Circles.route,
                    NavRoute.Recommendations.route
                ) || currentRoute?.startsWith("circle_map") == true

                Scaffold(
                    bottomBar = {
                        if (isMainBottomTab) {
                            PinamBottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { target ->
                                    navController.navigate(target) {
                                        popUpTo(NavRoute.Circles.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onDropPinClick = {},
                                activeCircleId = circleState.activeCircle?.id
                            )
                        }
                    },
                    floatingActionButton = {
                        if (isMainBottomTab) {
                            DropPinFab(
                                onClick = {
                                    val activeId = circleState.activeCircle?.id
                                    if (!activeId.isNullOrBlank()) {
                                        navController.navigate(NavRoute.DropPin.createRoute(activeId))
                                    } else {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please select or create a circle first!")
                                        }
                                    }
                                }
                            )
                        }
                    },
                    floatingActionButtonPosition = FabPosition.End,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = if (currentUserId.isNotBlank()) NavRoute.Circles.route else NavRoute.Auth.route
                        ) {
                            // Auth Screen
                            composable(NavRoute.Auth.route) {
                                LoginSignUpScreen(
                                    viewModel = authViewModel,
                                    onAuthSuccess = {
                                        navController.navigate(NavRoute.Circles.route) {
                                            popUpTo(NavRoute.Auth.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // Circles List Screen
                            composable(NavRoute.Circles.route) {
                                CirclesListScreen(
                                    viewModel = circleViewModel,
                                    currentUserId = currentUserId,
                                    onSelectCircleMap = { circleId ->
                                        navController.navigate(NavRoute.CircleMap.createRoute(circleId))
                                    },
                                    onOpenCircleDetails = { circleId ->
                                        navController.navigate(NavRoute.CircleDetails.createRoute(circleId))
                                    }
                                )
                            }

                            // Circle Map / Feed Screen
                            composable(
                                route = NavRoute.CircleMap.route,
                                arguments = listOf(navArgument("circleId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val circleId = backStackEntry.arguments?.getString("circleId") ?: ""
                                CircleMapScreen(
                                    circleId = circleId,
                                    pinViewModel = pinViewModel,
                                    circleViewModel = circleViewModel,
                                    onPinClick = { pinId ->
                                        navController.navigate(NavRoute.PinDetail.createRoute(pinId))
                                    },
                                    onDropPinClick = { targetCircleId ->
                                        navController.navigate(NavRoute.DropPin.createRoute(targetCircleId))
                                    }
                                )
                            }

                            // Drop a Pin Screen
                            composable(
                                route = NavRoute.DropPin.route,
                                arguments = listOf(navArgument("circleId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val circleId = backStackEntry.arguments?.getString("circleId") ?: ""
                                DropPinScreen(
                                    circleId = circleId,
                                    viewModel = pinViewModel,
                                    authorId = currentUserId,
                                    authorName = currentUserName,
                                    onBack = { navController.popBackStack() },
                                    onPinDropped = {
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // Pin Detail Screen
                            composable(
                                route = NavRoute.PinDetail.route,
                                arguments = listOf(navArgument("pinId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val pinId = backStackEntry.arguments?.getString("pinId") ?: ""
                                PinDetailScreen(
                                    pinId = pinId,
                                    pinViewModel = pinViewModel,
                                    currentUserId = currentUserId,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // Recommendations Screen
                            composable(NavRoute.Recommendations.route) {
                                RecommendationsScreen(
                                    viewModel = recommendationViewModel,
                                    currentUserId = currentUserId,
                                    onPinClick = { pinId ->
                                        navController.navigate(NavRoute.PinDetail.createRoute(pinId))
                                    }
                                )
                            }

                            // Circle Details / Members Screen
                            composable(
                                route = NavRoute.CircleDetails.route,
                                arguments = listOf(navArgument("circleId") { type = NavType.StringType })
                            ) { backStackEntry ->
                                val circleId = backStackEntry.arguments?.getString("circleId") ?: ""
                                CircleDetailsScreen(
                                    circleId = circleId,
                                    viewModel = circleViewModel,
                                    currentUserId = currentUserId,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
