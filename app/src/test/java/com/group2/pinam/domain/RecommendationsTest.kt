package com.group2.pinam.domain

import com.group2.pinam.model.Pin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationsTest {

    private val me = "user_me"

    @Test
    fun buildRecommendations_groupsCaseInsensitiveAndTrimsPlaceNames() {
        val pins = listOf(
            Pin(id = "1", placeName = "Cozy Cafe", authorId = "friend1", vibe = "Cozy"),
            Pin(id = "2", placeName = " cozy cafe  ", authorId = "friend2", vibe = "Cheap")
        )

        val recs = buildRecommendations(pins, me)

        assertEquals(1, recs.size)
        assertEquals("Cozy Cafe", recs[0].placeName)
        assertEquals(2, recs[0].friendCount)
        assertTrue(recs[0].vibes.containsAll(listOf("Cozy", "Cheap")))
    }

    @Test
    fun buildRecommendations_skipsPlacesUserHasBeenTo() {
        val pins = listOf(
            Pin(id = "1", placeName = "Visited Spot", authorId = "friend1", beenThere = listOf(me)),
            Pin(id = "2", placeName = "Unvisited Spot", authorId = "friend2", beenThere = listOf("friend1"))
        )

        val recs = buildRecommendations(pins, me)

        assertEquals(1, recs.size)
        assertEquals("Unvisited Spot", recs[0].placeName)
    }

    @Test
    fun buildRecommendations_countsReactionsAndPostsAndSortsByFriendCount() {
        val pins = listOf(
            // Popular spot: 3 distinct friends (friend1 author, friend2 beenThere, friend3 reaction)
            Pin(
                id = "1",
                placeName = "Popular Spot",
                authorId = "friend1",
                beenThere = listOf("friend2"),
                reactions = mapOf("friend3" to "heart")
            ),
            // Less popular spot: 1 friend
            Pin(
                id = "2",
                placeName = "Quiet Spot",
                authorId = "friend4"
            )
        )

        val recs = buildRecommendations(pins, me)

        assertEquals(2, recs.size)
        assertEquals("Popular Spot", recs[0].placeName)
        assertEquals(3, recs[0].friendCount)

        assertEquals("Quiet Spot", recs[1].placeName)
        assertEquals(1, recs[1].friendCount)
    }
}
