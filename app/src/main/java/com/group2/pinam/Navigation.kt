package com.group2.pinam

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.group2.pinam.ui.screens.AddPinScreen
import com.group2.pinam.ui.screens.CircleMapScreen
import com.group2.pinam.ui.screens.CirclesScreen
import com.group2.pinam.ui.screens.LoginScreen
import com.group2.pinam.ui.screens.PinDetailScreen
import com.group2.pinam.ui.screens.RecommendationsScreen

/** All routes live here. Add a route in a small PR and tell the group. */
object Routes {
    const val LOGIN = "login"
    const val CIRCLES = "circles"
    const val MAP = "map/{circleId}"
    const val ADD_PIN = "addPin/{circleId}"
    const val PIN = "pin/{circleId}/{pinId}"
    const val RECS = "recs/{circleId}"

    fun map(circleId: String) = "map/$circleId"
    fun addPin(circleId: String) = "addPin/$circleId"
    fun pin(circleId: String, pinId: String) = "pin/$circleId/$pinId"
    fun recs(circleId: String) = "recs/$circleId"
}

@Composable
fun PinamApp() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(onLoggedIn = { nav.navigate(Routes.CIRCLES) { popUpTo(Routes.LOGIN) { inclusive = true } } })
        }
        composable(Routes.CIRCLES) {
            CirclesScreen(onOpenCircle = { nav.navigate(Routes.map(it)) })
        }
        composable(Routes.MAP) { entry ->
            val circleId = entry.arguments?.getString("circleId") ?: return@composable
            CircleMapScreen(
                circleId = circleId,
                onBack = { nav.popBackStack() },
                onAddPin = { nav.navigate(Routes.addPin(circleId)) },
                onOpenPin = { nav.navigate(Routes.pin(circleId, it)) },
                onRecommendations = { nav.navigate(Routes.recs(circleId)) }
            )
        }
        composable(Routes.ADD_PIN) { entry ->
            val circleId = entry.arguments?.getString("circleId") ?: return@composable
            AddPinScreen(circleId, onDone = { nav.popBackStack() })
        }
        composable(Routes.PIN) { entry ->
            val circleId = entry.arguments?.getString("circleId") ?: return@composable
            val pinId = entry.arguments?.getString("pinId") ?: return@composable
            PinDetailScreen(circleId, pinId, onBack = { nav.popBackStack() })
        }
        composable(Routes.RECS) { entry ->
            val circleId = entry.arguments?.getString("circleId") ?: return@composable
            RecommendationsScreen(circleId, onBack = { nav.popBackStack() })
        }
    }
}
