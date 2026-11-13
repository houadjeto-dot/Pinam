package com.example.pinam.data.repository

import com.example.pinam.data.model.Circle
import com.example.pinam.data.model.User
import java.util.UUID

class CircleRepository {

    private fun generateInviteCode(): String {
        return UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
    }

    suspend fun createCircle(
        name: String,
        description: String,
        type: String = "FRIENDS",
        campusName: String = "",
        userId: String
    ): Result<Circle> {
        return try {
            val circleId = "circle_${UUID.randomUUID().toString().take(8)}"
            val inviteCode = generateInviteCode()
            val circle = Circle(
                id = circleId,
                name = name,
                description = description,
                inviteCode = inviteCode,
                memberIds = listOf(userId),
                ownerId = userId,
                type = type,
                campusName = campusName
            )
            InMemoryStore.circles.add(0, circle)
            LocalStoreManager.save()
            Result.success(circle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinCircleByCode(inviteCode: String, userId: String): Result<Circle> {
        return try {
            val codeToSearch = inviteCode.trim().uppercase()
            val circle = InMemoryStore.circles.find { it.inviteCode == codeToSearch }
                ?: throw IllegalArgumentException("Invalid invite code: $codeToSearch")

            val updatedMembers = if (circle.memberIds.contains(userId)) {
                circle.memberIds
            } else {
                circle.memberIds + userId
            }
            val updatedCircle = circle.copy(memberIds = updatedMembers)
            val index = InMemoryStore.circles.indexOfFirst { it.id == circle.id }
            if (index != -1) {
                InMemoryStore.circles[index] = updatedCircle
            }
            LocalStoreManager.save()
            Result.success(updatedCircle)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCircles(userId: String): Result<List<Circle>> {
        return try {
            val userCircles = InMemoryStore.circles.filter { it.memberIds.contains(userId) }
            Result.success(userCircles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCircleMembers(memberIds: List<String>): Result<List<User>> {
        return try {
            val members = memberIds.mapNotNull { id ->
                InMemoryStore.users[id] ?: User(id, "user_$id@example.com", "Member $id", "")
            }
            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCircle(circleId: String, name: String, description: String): Result<Unit> {
        return try {
            val index = InMemoryStore.circles.indexOfFirst { it.id == circleId }
            if (index != -1) {
                val circle = InMemoryStore.circles[index]
                InMemoryStore.circles[index] = circle.copy(name = name, description = description)
            }
            LocalStoreManager.save()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeMember(circleId: String, memberId: String): Result<Unit> {
        return try {
            val index = InMemoryStore.circles.indexOfFirst { it.id == circleId }
            if (index != -1) {
                val circle = InMemoryStore.circles[index]
                InMemoryStore.circles[index] = circle.copy(memberIds = circle.memberIds.filter { it != memberId })
            }
            LocalStoreManager.save()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun leaveCircle(circleId: String, userId: String): Result<Unit> {
        return removeMember(circleId, userId)
    }
}
