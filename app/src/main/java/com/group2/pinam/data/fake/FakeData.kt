package com.group2.pinam.data.fake

import com.group2.pinam.model.Circle
import com.group2.pinam.model.Pin

object FakeData {
    const val ME = "u1"

    val circles = listOf(
        Circle("c1", "Osu Crew", "OSU123", listOf("u1", "u2", "u3", "u4")),
        Circle("c2", "Uni Friends", "UNI456", listOf("u1", "u2", "u3"))
    )

    private val now = System.currentTimeMillis()

    val pins = listOf(
        Pin("p1", "c1", "u2", "Kofi", "", 5.5560, -0.1820, "Rooftop café", "Cozy", "Best iced coffee in Osu",
            now - 2 * 3_600_000, mapOf("u1" to "heart", "u3" to "star"), listOf("u3"), listOf("u1")),
        Pin("p2", "c1", "u3", "Efua", "", 5.5600, -0.1750, "Library nook", "Study spot", "Quiet and cheap",
            now - 5 * 3_600_000, emptyMap(), listOf("u2"), emptyList()),
        Pin("p3", "c1", "u4", "Yaw", "", 5.5480, -0.1900, "Sunset grill", "Date spot", "Great view",
            now - 26 * 3_600_000, mapOf("u2" to "heart"), listOf("u2", "u3"), emptyList()),
        Pin("p4", "c1", "u1", "Ama", "", 5.5530, -0.1700, "Beach walk", "Cheap", "Free and breezy",
            now - 48 * 3_600_000, emptyMap(), listOf("u1"), emptyList())
    )
}
