package com.example.pinam.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.pinam.ui.theme.*

data class CircleItem(
    val id: String,
    val name: String,
    val pinCountText: String,
    val gradientBrush: Brush,
    val memberAvatars: List<Pair<String, Color>>
)

data class PinItem(
    val id: String,
    val placeName: String,
    val vibeTag: String,
    val authorAndDate: String,
    val caption: String,
    val gradientBrush: Brush,
    val heartCount: Int = 12,
    val starCount: Int = 5,
    val smileyCount: Int = 3
)

data class DiscoverItem(
    val id: String,
    val placeName: String,
    val vibeTags: List<String>,
    val friendLovedText: String,
    val gradientBrush: Brush,
    val isBookmarked: Boolean
)

object SampleData {
    val sampleCircles = listOf(
        CircleItem(
            id = "osu_crew",
            name = "Osu Crew",
            pinCountText = "24 pins",
            gradientBrush = GradientOrangeBrush,
            memberAvatars = listOf("A" to AvatarOrange, "K" to AvatarGreen, "E" to AvatarPink, "Y" to AvatarPurple)
        ),
        CircleItem(
            id = "uni_friends",
            name = "Uni Friends",
            pinCountText = "11 pins",
            gradientBrush = GradientTealBrush,
            memberAvatars = listOf("A" to AvatarOrange, "K" to AvatarGreen, "E" to AvatarPink)
        ),
        CircleItem(
            id = "cousins",
            name = "Cousins",
            pinCountText = "37 pins",
            gradientBrush = GradientPinkBrush,
            memberAvatars = listOf("A" to AvatarOrange, "K" to AvatarGreen, "E" to AvatarPink, "Y" to AvatarPurple)
        )
    )

    val sampleRecentPins = listOf(
        PinItem("p1", "Brunch spot", "Cozy", "Kofi · 2h ago", "Great waffles!", GradientOrangeBrush),
        PinItem("p2", "Beach walk", "Cheap", "Ama · 4h ago", "Beautiful sunset view.", GradientPinkBrush),
        PinItem("p3", "Library nook", "Study spot", "Esi · 1d ago", "Quiet place to code.", GradientPurpleBrush)
    )

    val rooftopCafePin = PinItem(
        id = "rooftop_cafe",
        placeName = "Rooftop café",
        vibeTag = "Cozy",
        authorAndDate = "Kofi · 2h ago",
        caption = "Best iced coffee in Osu.",
        gradientBrush = GradientTealBrush,
        heartCount = 12,
        starCount = 5,
        smileyCount = 3
    )

    val discoverPicks = listOf(
        DiscoverItem("d1", "Rooftop café", listOf("Cozy"), "3 of your friends loved this", GradientTealBrush, isBookmarked = false),
        DiscoverItem("d2", "Library nook", listOf("Study spot", "Cheap"), "2 of your friends loved this", GradientPurpleBrush, isBookmarked = true),
        DiscoverItem("d3", "Sunset grill", listOf("Date spot"), "2 of your friends loved this", GradientPinkBrush, isBookmarked = false),
        DiscoverItem("d4", "Beach walk", listOf("Cheap"), "1 of your friends loved this", GradientOrangeBrush, isBookmarked = false)
    )
}
