package com.example.pinam.data.repository

import com.example.pinam.data.model.Reaction
import java.util.UUID

class ReactionRepository {

    suspend fun getReactionsForPin(pinId: String): Result<List<Reaction>> {
        return try {
            val list = InMemoryStore.reactions.filter { it.pinId == pinId }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReactionsForPins(pinIds: List<String>): Result<List<Reaction>> {
        if (pinIds.isEmpty()) return Result.success(emptyList())
        return try {
            val list = InMemoryStore.reactions.filter { pinIds.contains(it.pinId) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleReaction(pinId: String, userId: String, type: String): Result<Unit> {
        return try {
            val existing = InMemoryStore.reactions.find {
                it.pinId == pinId && it.userId == userId && it.type == type
            }
            if (existing != null) {
                InMemoryStore.reactions.remove(existing)
            } else {
                val reaction = Reaction(
                    id = "r_${UUID.randomUUID().toString().take(8)}",
                    pinId = pinId,
                    userId = userId,
                    type = type
                )
                InMemoryStore.reactions.add(reaction)
            }
            LocalStoreManager.save()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
