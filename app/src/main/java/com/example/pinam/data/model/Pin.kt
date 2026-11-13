package com.example.pinam.data.model

data class Pin(
    val id: String = "",
    val circleId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val photoUrl: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val placeName: String = "",
    val caption: String = "",
    val vibeTag: String = "",
    val vibeTags: List<String> = emptyList(),
    val landmarkNote: String = "",
    val ghanaPostAddress: String = "",
    val transportTip: String = "",
    val budgetRange: String = "", // "UNDER_20" | "FROM_20_TO_50" | "OVER_50"
    val createdAt: Long = System.currentTimeMillis()
) {
    val displayVibeTags: List<String>
        get() = if (vibeTags.isNotEmpty()) vibeTags else if (vibeTag.isNotBlank()) listOf(vibeTag) else emptyList()

    val budgetDisplay: String
        get() = when (budgetRange) {
            "UNDER_20" -> "Under GH₵20"
            "FROM_20_TO_50" -> "GH₵20-50"
            "OVER_50" -> "GH₵50+"
            else -> ""
        }
}
