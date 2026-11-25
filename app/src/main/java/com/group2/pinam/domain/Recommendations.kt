package com.group2.pinam.domain

import com.group2.pinam.model.Pin

data class Recommendation(
    val placeName: String,
    val vibes: List<String>,
    val photoUrl: String,
    val friendCount: Int
)

/**
 * Recommendations are CALCULATED, not stored.
 * Groups pins by normalized place name (trimmed, lowercase), excludes places the user
 * has already been to (user is in beenThere), and counts distinct friends who posted,
 * were there, or reacted. Sorted by friendCount descending.
 */
fun buildRecommendations(pins: List<Pin>, me: String): List<Recommendation> =
    pins.groupBy { it.placeName.trim().lowercase() }
        .filterValues { ps -> ps.none { me in it.beenThere } }
        .mapNotNull { (_, ps) ->
            val friends = (
                ps.map { it.authorId } +
                ps.flatMap { it.beenThere } +
                ps.flatMap { it.reactions.keys }
            ).filter { it != me && it.isNotBlank() }.toSet()

            if (friends.isEmpty()) {
                null
            } else {
                val displayName = ps.firstOrNull { it.placeName.isNotBlank() }?.placeName?.trim() ?: ""
                val photoUrl = ps.firstOrNull { it.photoUrl.isNotBlank() }?.photoUrl ?: ""
                val vibes = ps.map { it.vibe }.filter { it.isNotBlank() }.distinct()
                Recommendation(
                    placeName = displayName,
                    vibes = vibes,
                    photoUrl = photoUrl,
                    friendCount = friends.size
                )
            }
        }
        .sortedByDescending { it.friendCount }
