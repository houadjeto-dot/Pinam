package com.group2.pinam.model

// All properties have default values so Firestore can turn documents into these classes
// with .toObject<Pin>() later. Do NOT remove the defaults.

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val avatarUrl: String = ""
)

data class Circle(
    val id: String = "",
    val name: String = "",
    val inviteCode: String = "",
    val memberIds: List<String> = emptyList()
)

data class Pin(
    val id: String = "",
    val circleId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val photoUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val placeName: String = "",
    val vibe: String = Vibe.COZY.label,
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val reactions: Map<String, String> = emptyMap(),   // userId -> "heart" | "star" | "smile"
    val beenThere: List<String> = emptyList(),          // userIds
    val wantToGo: List<String> = emptyList()            // userIds
)

enum class Vibe(val label: String) {
    COZY("Cozy"), CHEAP("Cheap"), DATE_SPOT("Date spot"), STUDY_SPOT("Study spot");

    companion object {
        fun fromLabel(s: String): Vibe = entries.firstOrNull { it.label == s } ?: COZY
    }
}
