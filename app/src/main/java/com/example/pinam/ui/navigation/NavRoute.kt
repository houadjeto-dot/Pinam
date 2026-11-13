package com.example.pinam.ui.navigation

sealed class NavRoute(val route: String) {
    object Auth : NavRoute("auth")
    object Circles : NavRoute("circles")
    object CircleMap : NavRoute("circle_map/{circleId}") {
        fun createRoute(circleId: String) = "circle_map/$circleId"
    }
    object DropPin : NavRoute("drop_pin/{circleId}") {
        fun createRoute(circleId: String) = "drop_pin/$circleId"
    }
    object PinDetail : NavRoute("pin_detail/{pinId}") {
        fun createRoute(pinId: String) = "pin_detail/$pinId"
    }
    object Recommendations : NavRoute("recommendations")
    object CircleDetails : NavRoute("circle_details/{circleId}") {
        fun createRoute(circleId: String) = "circle_details/$circleId"
    }
}
