package com.example.pinam.data.repository

import android.content.Context
import com.example.pinam.data.model.Circle
import com.example.pinam.data.model.Pin
import com.example.pinam.data.model.Reaction
import com.example.pinam.data.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object LocalStoreManager {
    private const val FILE_NAME = "pinam_local_db.json"
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        val file = File(context.filesDir, FILE_NAME)
        if (file.exists()) {
            try {
                val jsonStr = file.readText()
                if (jsonStr.isNotBlank()) {
                    loadFromJson(jsonStr)
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Save initial default seed data if file doesn't exist
        save()
    }

    fun save() {
        val ctx = appContext ?: return
        try {
            val root = JSONObject()

            // Current User
            InMemoryStore.currentUser?.let { user ->
                root.put("currentUser", userToJson(user))
            }

            // Users
            val usersArray = JSONArray()
            InMemoryStore.users.values.forEach { user ->
                usersArray.put(userToJson(user))
            }
            root.put("users", usersArray)

            // Circles
            val circlesArray = JSONArray()
            InMemoryStore.circles.forEach { circle ->
                circlesArray.put(circleToJson(circle))
            }
            root.put("circles", circlesArray)

            // Pins
            val pinsArray = JSONArray()
            InMemoryStore.pins.forEach { pin ->
                pinsArray.put(pinToJson(pin))
            }
            root.put("pins", pinsArray)

            // Reactions
            val reactionsArray = JSONArray()
            InMemoryStore.reactions.forEach { reaction ->
                reactionsArray.put(reactionToJson(reaction))
            }
            root.put("reactions", reactionsArray)

            val file = File(ctx.filesDir, FILE_NAME)
            file.writeText(root.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFromJson(jsonStr: String) {
        val root = JSONObject(jsonStr)

        // Current User
        if (root.has("currentUser")) {
            InMemoryStore.currentUser = jsonToUser(root.getJSONObject("currentUser"))
        }

        // Users
        if (root.has("users")) {
            val usersArray = root.getJSONArray("users")
            for (i in 0 until usersArray.length()) {
                val user = jsonToUser(usersArray.getJSONObject(i))
                InMemoryStore.users[user.id] = user
            }
        }

        // Circles
        if (root.has("circles")) {
            val circlesArray = root.getJSONArray("circles")
            InMemoryStore.circles.clear()
            for (i in 0 until circlesArray.length()) {
                InMemoryStore.circles.add(jsonToCircle(circlesArray.getJSONObject(i)))
            }
        }

        // Pins
        if (root.has("pins")) {
            val pinsArray = root.getJSONArray("pins")
            InMemoryStore.pins.clear()
            for (i in 0 until pinsArray.length()) {
                InMemoryStore.pins.add(jsonToPin(pinsArray.getJSONObject(i)))
            }
        }

        // Reactions
        if (root.has("reactions")) {
            val reactionsArray = root.getJSONArray("reactions")
            InMemoryStore.reactions.clear()
            for (i in 0 until reactionsArray.length()) {
                InMemoryStore.reactions.add(jsonToReaction(reactionsArray.getJSONObject(i)))
            }
        }
    }

    // JSON Converters
    private fun userToJson(user: User): JSONObject {
        return JSONObject().apply {
            put("id", user.id)
            put("email", user.email)
            put("displayName", user.displayName)
            put("photoUrl", user.photoUrl)
        }
    }

    private fun jsonToUser(obj: JSONObject): User {
        return User(
            id = obj.optString("id", ""),
            email = obj.optString("email", ""),
            displayName = obj.optString("displayName", ""),
            photoUrl = obj.optString("photoUrl", "")
        )
    }

    private fun circleToJson(circle: Circle): JSONObject {
        val memberIdsArr = JSONArray()
        circle.memberIds.forEach { memberIdsArr.put(it) }
        return JSONObject().apply {
            put("id", circle.id)
            put("name", circle.name)
            put("description", circle.description)
            put("inviteCode", circle.inviteCode)
            put("memberIds", memberIdsArr)
            put("ownerId", circle.ownerId)
            put("type", circle.type)
            put("campusName", circle.campusName)
        }
    }

    private fun jsonToCircle(obj: JSONObject): Circle {
        val memberIdsList = mutableListOf<String>()
        if (obj.has("memberIds")) {
            val arr = obj.getJSONArray("memberIds")
            for (i in 0 until arr.length()) {
                memberIdsList.add(arr.getString(i))
            }
        }
        return Circle(
            id = obj.optString("id", ""),
            name = obj.optString("name", ""),
            description = obj.optString("description", ""),
            inviteCode = obj.optString("inviteCode", ""),
            memberIds = memberIdsList,
            ownerId = obj.optString("ownerId", ""),
            type = obj.optString("type", "FRIENDS"),
            campusName = obj.optString("campusName", "")
        )
    }

    private fun pinToJson(pin: Pin): JSONObject {
        val tagsArr = JSONArray()
        pin.displayVibeTags.forEach { tagsArr.put(it) }
        return JSONObject().apply {
            put("id", pin.id)
            put("circleId", pin.circleId)
            put("authorId", pin.authorId)
            put("authorName", pin.authorName)
            put("photoUrl", pin.photoUrl)
            put("lat", pin.lat)
            put("lng", pin.lng)
            put("placeName", pin.placeName)
            put("caption", pin.caption)
            put("vibeTag", pin.vibeTag)
            put("vibeTags", tagsArr)
            put("landmarkNote", pin.landmarkNote)
            put("ghanaPostAddress", pin.ghanaPostAddress)
            put("transportTip", pin.transportTip)
            put("budgetRange", pin.budgetRange)
            put("createdAt", pin.createdAt)
        }
    }

    private fun jsonToPin(obj: JSONObject): Pin {
        val tagsList = mutableListOf<String>()
        if (obj.has("vibeTags")) {
            val arr = obj.getJSONArray("vibeTags")
            for (i in 0 until arr.length()) {
                tagsList.add(arr.getString(i))
            }
        }
        return Pin(
            id = obj.optString("id", ""),
            circleId = obj.optString("circleId", ""),
            authorId = obj.optString("authorId", ""),
            authorName = obj.optString("authorName", ""),
            photoUrl = obj.optString("photoUrl", ""),
            lat = obj.optDouble("lat", 0.0),
            lng = obj.optDouble("lng", 0.0),
            placeName = obj.optString("placeName", ""),
            caption = obj.optString("caption", ""),
            vibeTag = obj.optString("vibeTag", ""),
            vibeTags = tagsList,
            landmarkNote = obj.optString("landmarkNote", ""),
            ghanaPostAddress = obj.optString("ghanaPostAddress", ""),
            transportTip = obj.optString("transportTip", ""),
            budgetRange = obj.optString("budgetRange", ""),
            createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        )
    }

    private fun reactionToJson(reaction: Reaction): JSONObject {
        return JSONObject().apply {
            put("id", reaction.id)
            put("pinId", reaction.pinId)
            put("userId", reaction.userId)
            put("type", reaction.type)
        }
    }

    private fun jsonToReaction(obj: JSONObject): Reaction {
        return Reaction(
            id = obj.optString("id", ""),
            pinId = obj.optString("pinId", ""),
            userId = obj.optString("userId", ""),
            type = obj.optString("type", "been_there")
        )
    }
}
