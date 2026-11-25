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
            awaitClose { }
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
            awaitClose { }
            return@callbackFlow
        }
        val reg = db.collection("circles").document(circleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || (snapshot == null) || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val circle = snapshot.toObject(Circle::class.java)?.copy(id = snapshot.id)
                trySend(circle)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun createCircle(name: String, owner: User): Result<Circle> = runCatching {
        val docRef = db.collection("circles").document()
        val code = (1..6).map { ('A'..'Z').random() }.joinToString("")
        val circle = Circle(
            id = docRef.id,
            name = name,
            inviteCode = code,
            memberIds = listOf(owner.id),
        )
        docRef.set(circle).await()
        circle
    }

    override suspend fun joinByCode(code: String, user: User): Result<Circle> = runCatching {
        val cleanCode = code.trim().uppercase()
        val snap = db.collection("circles")
            .whereEqualTo("inviteCode", cleanCode)
            .limit(1)
            .get()
            .await()

        val doc = snap.documents.firstOrNull() ?: throw IllegalArgumentException("No circle with that code")
        doc.reference.update("memberIds", FieldValue.arrayUnion(user.id)).await()

        val updatedDoc = doc.reference.get().await()
        updatedDoc.toObject(Circle::class.java)?.copy(id = doc.id)
            ?: throw IllegalStateException("Failed to parse joined circle")
    }

    override suspend fun removeMember(circleId: String, userId: String): Result<Unit> = runCatching {
        db.collection("circles").document(circleId)
            .update("memberIds", FieldValue.arrayRemove(userId))
            .await()
    }
}
