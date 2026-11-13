package com.group2.pinam.domain

import com.group2.pinam.model.Pin

data class Recommendation(
    val placeName: String,
    val vibes: List<String>,
    val photoUrl: String,
    val friendCount: Int
)

/**
 * Recommendations are CALCULATED, not stored (as in the brief).
 * Starter version: group friends' pins by place, count distinct friends who posted or
 * marked "been there", most popular first. Pair 5 improves the ranking
 * (e.g. weight by how close the friends are).
 */
fun buildRecommendations(pins: List<Pin>, me: String): List<Recommendation> =
    pins.filter { it.authorId != me }
        .groupBy { it.placeName }
        .map { (place, ps) ->
            val friends = (ps.map { it.authorId } + ps.flatMap { it.beenThere }).filter { it != me }.toSet()
            Recommendation(place, ps.map { it.vibe }.distinct(), ps.first().photoUrl, friends.size)
        }
        .sortedByDescending { it.friendCount }
