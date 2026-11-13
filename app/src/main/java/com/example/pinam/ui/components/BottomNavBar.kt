package com.example.pinam.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Recommend
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pinam.ui.navigation.NavRoute
import com.example.pinam.ui.theme.CoralPrimary

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Circles : BottomNavItem(NavRoute.Circles.route, "Circles", Icons.Default.Group)
    object Map : BottomNavItem("circle_map_tab", "Circle Map", Icons.Default.Map)
    object Recommendations : BottomNavItem(NavRoute.Recommendations.route, "Recs", Icons.Default.Recommend)
}

@Composable
fun PinamBottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onDropPinClick: () -> Unit,
    activeCircleId: String? = null
) {
    val items = listOf(
        BottomNavItem.Circles,
        BottomNavItem.Map,
        BottomNavItem.Recommendations
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val isSelected = when (item) {
                BottomNavItem.Circles -> currentRoute == NavRoute.Circles.route
                BottomNavItem.Map -> currentRoute?.startsWith("circle_map") == true
                BottomNavItem.Recommendations -> currentRoute == NavRoute.Recommendations.route
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    val targetRoute = if (item == BottomNavItem.Map) {
                        if (activeCircleId != null) NavRoute.CircleMap.createRoute(activeCircleId)
                        else NavRoute.Circles.route
                    } else {
                        item.route
                    }
                    onNavigate(targetRoute)
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CoralPrimary,
                    selectedTextColor = CoralPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Composable
fun DropPinFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = CoralPrimary,
        contentColor = Color.White,
        modifier = modifier
    ) {
        Icon(Icons.Default.Add, contentDescription = "Drop a Pin")
    }
}
