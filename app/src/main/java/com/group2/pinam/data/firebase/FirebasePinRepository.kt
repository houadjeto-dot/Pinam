package com.group2.pinam.data.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.group2.pinam.data.PinRepository
import com.group2.pinam.model.Pin
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePinRepository : PinRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun observePins(circleId: String): Flow<List<Pin>> = callbackFlow {
        val reg = db.collection("circles").document(circleId).collection("pins")
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    Log.e("FirebasePinRepository", "Error observing pins", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(
                    snap?.documents?.mapNotNull { d ->
                        d.toObject(Pin::class.java)?.copy(id = d.id)
                    }?.sortedByDescending { it.createdAt } ?: emptyList()
                )
            }
        awaitClose { reg.remove() }
    }

    override fun observePin(circleId: String, pinId: String): Flow<Pin?> = callbackFlow {
        val reg = db.collection("circles").document(circleId).collection("pins").document(pinId)
            .addSnapshotListener { snap, error ->
                if (error != null || snap == null || !snap.exists()) {
                    if (error != null) {
                        Log.e("FirebasePinRepository", "Error observing pin", error)
                    }
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snap.toObject(Pin::class.java)?.copy(id = snap.id))
            }
        awaitClose { reg.remove() }
    }

    override suspend fun addPin(pin: Pin, photoUri: String?): Result<Pin> = runCatching {
        val ref = db.collection("circles").document(pin.circleId).collection("pins").document()
        var url = ""
        if (photoUri != null) {
            val photoRef = FirebaseStorage.getInstance().reference.child("pins/${ref.id}.jpg")
            photoRef.putFile(Uri.parse(photoUri)).await()
            url = photoRef.downloadUrl.await().toString()
        }
        val saved = pin.copy(id = ref.id, photoUrl = url)
        ref.set(saved).await()
        saved
    }

    override suspend fun updatePin(pin: Pin): Result<Unit> = runCatching {
        db.collection("circles").document(pin.circleId).collection("pins").document(pin.id)
            .update(mapOf("caption" to pin.caption, "vibe" to pin.vibe))
            .await()
    }

    override suspend fun deletePin(circleId: String, pinId: String): Result<Unit> = runCatching {
        db.collection("circles").document(circleId).collection("pins").document(pinId).delete().await()
    }

    override suspend fun setReaction(circleId: String, pinId: String, userId: String, type: String?) {
        try {
            val ref = db.collection("circles").document(circleId).collection("pins").document(pinId)
            if (type == null) {
                ref.update("reactions.$userId", FieldValue.delete()).await()
            } else {
                ref.update("reactions.$userId", type).await()
            }
        } catch (e: Exception) {
            Log.e("FirebasePinRepository", "Error setting reaction", e)
        }
    }

    override suspend fun toggleBeenThere(circleId: String, pinId: String, userId: String) {
        try {
            val ref = db.collection("circles").document(circleId).collection("pins").document(pinId)
            db.runTransaction { tx ->
                val snap = tx.get(ref)
                val pin = snap.toObject(Pin::class.java)
                if (pin != null) {
                    if (userId in pin.beenThere) {
                        tx.update(ref, "beenThere", FieldValue.arrayRemove(userId))
                    } else {
                        tx.update(ref, "beenThere", FieldValue.arrayUnion(userId))
                    }
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebasePinRepository", "Error toggling been there", e)
        }
    }

    override suspend fun toggleWantToGo(circleId: String, pinId: String, userId: String) {
        try {
            val ref = db.collection("circles").document(circleId).collection("pins").document(pinId)
            db.runTransaction { tx ->
                val snap = tx.get(ref)
                val pin = snap.toObject(Pin::class.java)
                if (pin != null) {
                    if (userId in pin.wantToGo) {
                        tx.update(ref, "wantToGo", FieldValue.arrayRemove(userId))
                    } else {
                        tx.update(ref, "wantToGo", FieldValue.arrayUnion(userId))
                    }
                }
            }.await()
        } catch (e: Exception) {
            Log.e("FirebasePinRepository", "Error toggling want to go", e)
        }
    }
}
