package com.group2.pinam.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.group2.pinam.model.Pin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePinRepository : PinRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun observePins(circleId: String): Flow<List<Pin>> = callbackFlow {
        val listener = db.collection("pins")
            .whereEqualTo("circleId", circleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val pins = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Pin::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(pins.sortedByDescending { it.createdAt })
            }
        awaitClose { listener.remove() }
    }

    override fun observePin(circleId: String, pinId: String): Flow<Pin?> = callbackFlow {
        val listener = db.collection("pins").document(pinId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val pin = snapshot.toObject(Pin::class.java)?.copy(id = snapshot.id)
                trySend(pin)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addPin(pin: Pin, photoUri: String?): Result<Pin> = try {
        var finalPhotoUrl = pin.photoUrl
        if (!photoUri.isNullOrEmpty()) {
            val uri = Uri.parse(photoUri)
            val photoRef = storage.reference.child("pin_photos/${System.currentTimeMillis()}.jpg")
            photoRef.putFile(uri).await()
            finalPhotoUrl = photoRef.downloadUrl.await().toString()
        }
        val docRef = db.collection("pins").document()
        val newPin = pin.copy(
            id = docRef.id,
            photoUrl = finalPhotoUrl,
            createdAt = System.currentTimeMillis()
        )
        docRef.set(newPin).await()
        Result.success(newPin)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updatePin(pin: Pin): Result<Unit> = try {
        db.collection("pins").document(pin.id).set(pin).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deletePin(circleId: String, pinId: String): Result<Unit> = try {
        db.collection("pins").document(pinId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun setReaction(circleId: String, pinId: String, userId: String, type: String?) {
        val docRef = db.collection("pins").document(pinId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val pin = snapshot.toObject(Pin::class.java) ?: return@runTransaction
            val newReactions = if (type == null) {
                pin.reactions - userId
            } else {
                pin.reactions + (userId to type)
            }
            transaction.update(docRef, "reactions", newReactions)
        }.await()
    }

    override suspend fun toggleBeenThere(circleId: String, pinId: String, userId: String) {
        val docRef = db.collection("pins").document(pinId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val pin = snapshot.toObject(Pin::class.java) ?: return@runTransaction
            val newBeen = if (userId in pin.beenThere) {
                pin.beenThere - userId
            } else {
                pin.beenThere + userId
            }
            transaction.update(docRef, "beenThere", newBeen)
        }.await()
    }

    override suspend fun toggleWantToGo(circleId: String, pinId: String, userId: String) {
        val docRef = db.collection("pins").document(pinId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val pin = snapshot.toObject(Pin::class.java) ?: return@runTransaction
            val newWant = if (userId in pin.wantToGo) {
                pin.wantToGo - userId
            } else {
                pin.wantToGo + userId
            }
            transaction.update(docRef, "wantToGo", newWant)
        }.await()
    }
}
