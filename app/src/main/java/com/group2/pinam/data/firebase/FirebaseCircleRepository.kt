package com.group2.pinam.data.firebase

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.group2.pinam.data.CircleRepository
import com.group2.pinam.model.Circle
import com.group2.pinam.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseCircleRepository : CircleRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun observeMyCircles(userId: String): Flow<List<Circle>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val reg = db.collection("circles")
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { d ->
                    d.toObject(Circle::class.java)?.copy(id = d.id)
                } ?: emptyList()
                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    override fun observeCircle(circleId: String): Flow<Circle?> = callbackFlow {
        if (circleId.isBlank()) {
            trySend(null)
            awaitClose {}
            return@callbackFlow
        }

        val reg = db.collection("circles").document(circleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val circle = if (snapshot != null && snapshot.exists()) {
                    snapshot.toObject(Circle::class.java)?.copy(id = snapshot.id)
                } else null
                trySend(circle)
            }

        awaitClose { reg.remove() }
    }

    private fun generateInviteCode(): String {
        val allowedChars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
    }

    override suspend fun createCircle(name: String, owner: User): Result<Circle> {
        return runCatching {
            var code = generateInviteCode()
            var attempts = 0
            while (attempts < 5) {
                val existing = db.collection("circles")
                    .whereEqualTo("inviteCode", code)
                    .get()
                    .await()
                if (existing.isEmpty) break
                code = generateInviteCode()
                attempts++
            }

            val docRef = db.collection("circles").document()
            val circleData = mapOf(
                "id" to docRef.id,
                "name" to name,
                "inviteCode" to code,
                "memberIds" to listOf(owner.id),
                "ownerId" to owner.id,
                "createdAt" to System.currentTimeMillis()
            )
            docRef.set(circleData).await()

            Circle(
                id = docRef.id,
                name = name,
                inviteCode = code,
                memberIds = listOf(owner.id)
            )
        }
    }

    override suspend fun joinByCode(code: String, user: User): Result<Circle> {
        return runCatching {
            val cleanCode = code.trim().uppercase()
            val query = db.collection("circles")
                .whereEqualTo("inviteCode", cleanCode)
                .limit(1)
                .get()
                .await()

            val doc = query.documents.firstOrNull()
                ?: throw IllegalArgumentException("No circle with that code")

            db.collection("circles").document(doc.id)
                .update("memberIds", FieldValue.arrayUnion(user.id))
                .await()

            val updatedDoc = db.collection("circles").document(doc.id).get().await()
            updatedDoc.toObject(Circle::class.java)?.copy(id = doc.id)
                ?: throw IllegalStateException("Failed to load joined circle")
        }
    }

    override suspend fun removeMember(circleId: String, userId: String): Result<Unit> {
        return runCatching {
            db.collection("circles").document(circleId)
                .update("memberIds", FieldValue.arrayRemove(userId))
                .await()
            Unit
        }
    }
}
