package com.example.pinam.data.repository

import com.example.pinam.data.model.Circle
import com.example.pinam.data.model.Pin
import com.example.pinam.data.model.Reaction
import com.example.pinam.data.model.User

object InMemoryStore {
    var currentUser: User? = null
    val users = mutableMapOf<String, User>()
    val circles = mutableListOf<Circle>()
    val pins = mutableListOf<Pin>()
    val reactions = mutableListOf<Reaction>()

    init {
        // Pre-populate sample test data
        val demoUser1 = User("demo_user_1", "alex@example.com", "Alex", "")
        val demoUser2 = User("demo_user_2", "sam@example.com", "Sam", "")
        val demoUser3 = User("demo_user_3", "taylor@example.com", "Taylor", "")

        users[demoUser1.id] = demoUser1
        users[demoUser2.id] = demoUser2
        users[demoUser3.id] = demoUser3

        val sampleCircle = Circle(
            id = "circle_demo_1",
            name = "Weekend Explorers ☕",
            description = "Best cafes and hangouts with friends",
            inviteCode = "PIN123",
            memberIds = listOf("demo_user_1", "demo_user_2", "demo_user_3"),
            ownerId = "demo_user_1"
        )
        circles.add(sampleCircle)

        val samplePin1 = Pin(
            id = "pin_demo_1",
            circleId = "circle_demo_1",
            authorId = "demo_user_2",
            authorName = "Sam",
            photoUrl = "https://picsum.photos/id/1060/800/600",
            lat = 37.7749,
            lng = -122.4194,
            placeName = "Cozy Corner Bakery",
            caption = "Best matcha latte and sourdough in town! 🍵",
            vibeTag = "cozy",
            createdAt = System.currentTimeMillis() - 86400000
        )
        val samplePin2 = Pin(
            id = "pin_demo_2",
            circleId = "circle_demo_1",
            authorId = "demo_user_3",
            authorName = "Taylor",
            photoUrl = "https://picsum.photos/id/1080/800/600",
            lat = 37.7833,
            lng = -122.4167,
            placeName = "Sunset Park Overlook",
            caption = "Amazing spot for study sessions and sunsets 🌅",
            vibeTag = "study spot",
            createdAt = System.currentTimeMillis() - 43200000
        )
        pins.add(samplePin1)
        pins.add(samplePin2)

        reactions.add(Reaction("r1", "pin_demo_1", "demo_user_1", "been_there"))
        reactions.add(Reaction("r2", "pin_demo_1", "demo_user_3", "been_there"))
        reactions.add(Reaction("r3", "pin_demo_2", "demo_user_1", "want_to_go"))
    }

    fun addCircleUserIfLoggedIn(circleId: String) {
        val uid = currentUser?.id ?: return
        val circle = circles.find { it.id == circleId } ?: return
        if (!circle.memberIds.contains(uid)) {
            val idx = circles.indexOf(circle)
            circles[idx] = circle.copy(memberIds = circle.memberIds + uid)
        }
    }
}
