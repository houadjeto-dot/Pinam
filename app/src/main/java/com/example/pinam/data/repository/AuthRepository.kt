package com.example.pinam.data.repository

import com.example.pinam.data.model.User
import java.util.UUID

class AuthRepository {

    val currentUserId: String?
        get() = InMemoryStore.currentUser?.id

    suspend fun signUp(email: String, password: String, displayName: String): Result<User> {
        return try {
            val uid = "user_${UUID.randomUUID().toString().take(8)}"
            val user = User(
                id = uid,
                email = email,
                displayName = displayName.ifBlank { email.substringBefore("@") },
                photoUrl = ""
            )
            InMemoryStore.users[uid] = user
            InMemoryStore.currentUser = user
            LocalStoreManager.save()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val existingUser = InMemoryStore.users.values.find { it.email.equals(email, ignoreCase = true) }
            val user = existingUser ?: User(
                id = "user_${UUID.randomUUID().toString().take(8)}",
                email = email,
                displayName = email.substringBefore("@")
            )
            InMemoryStore.users[user.id] = user
            InMemoryStore.currentUser = user
            LocalStoreManager.save()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        InMemoryStore.currentUser = null
        LocalStoreManager.save()
    }

    suspend fun getCurrentUserData(): User? {
        return InMemoryStore.currentUser
    }
}
