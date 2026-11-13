package com.example.pinam.data.model

data class Circle(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val inviteCode: String = "",
    val memberIds: List<String> = emptyList(),
    val ownerId: String = "",
    val type: String = "FRIENDS", // HOSTEL_MATES | COURSE_MATES | CHURCH_YOUTH | FAMILY | FRIENDS | OTHER
    val campusName: String = ""
) {
    val typeDisplayName: String
        get() = when (type) {
            "HOSTEL_MATES" -> "Hostel Mates 🏠"
            "COURSE_MATES" -> "Course Mates 📚"
            "CHURCH_YOUTH" -> "Church Youth ⛪"
            "FAMILY" -> "Family 👨‍👩‍👧‍👦"
            "FRIENDS" -> "Friends 🤝"
            else -> "Other ✨"
        }

    val typeIconEmoji: String
        get() = when (type) {
            "HOSTEL_MATES" -> "🏠"
            "COURSE_MATES" -> "📚"
            "CHURCH_YOUTH" -> "⛪"
            "FAMILY" -> "👨‍👩‍👧‍👦"
            "FRIENDS" -> "🤝"
            else -> "✨"
        }
}
