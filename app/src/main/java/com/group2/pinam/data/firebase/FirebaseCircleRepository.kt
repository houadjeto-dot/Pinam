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


    override fun observeMyCircles(userId: String): Flow<List<Circle>> = callbackFlow {
            trySend(emptyList())
            awaitClose { }
            return@callbackFlow
        }
            .whereArrayContains("memberIds", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                }
    }

    override fun observeCircle(circleId: String): Flow<Circle?> = callbackFlow {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                    trySend(circle)
                }
            }
    }

    override suspend fun createCircle(name: String, owner: User): Result<Circle> {
            var attempts = 0
                val existing = db.collection("circles")
                    .whereEqualTo("inviteCode", code)
                    .get()
                    .await()
            }

                name = name,
                inviteCode = code,
                memberIds = listOf(owner.id)
            )
        }
    }

    override suspend fun joinByCode(code: String, user: User): Result<Circle> {
            val cleanCode = code.trim().uppercase()
                .whereEqualTo("inviteCode", cleanCode)
                .limit(1)
                .get()
                .await()


                .update("memberIds", FieldValue.arrayUnion(user.id))
                .await()

        }
    }

    override suspend fun removeMember(circleId: String, userId: String): Result<Unit> {
            db.collection("circles").document(circleId)
                .update("memberIds", FieldValue.arrayRemove(userId))
                .await()
        }
    }
}
