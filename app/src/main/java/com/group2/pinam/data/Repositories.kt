package com.group2.pinam.data

import com.group2.pinam.model.Circle
import com.group2.pinam.model.Pin
import com.group2.pinam.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * THE CONTRACT between screens and data.
 * Screen people code against these interfaces (using the Fake versions).
 * Data people write the Firebase versions. Change an interface only through a small PR
 * that the whole group knows about.
 */

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun signUp(name: String, email: String, password: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    fun signOut()
}

interface CircleRepository {
    fun observeMyCircles(userId: String): Flow<List<Circle>>
    fun observeCircle(circleId: String): Flow<Circle?>
    suspend fun createCircle(name: String, owner: User): Result<Circle>
    suspend fun joinByCode(code: String, user: User): Result<Circle>
    /** Also used for "leave circle" (remove yourself). */
    suspend fun removeMember(circleId: String, userId: String): Result<Unit>
}

interface PinRepository {
    fun observePins(circleId: String): Flow<List<Pin>>
    fun observePin(circleId: String, pinId: String): Flow<Pin?>
    /** photoUri = local image to upload (Firebase version uploads it and fills photoUrl). */
    suspend fun addPin(pin: Pin, photoUri: String?): Result<Pin>
    suspend fun updatePin(pin: Pin): Result<Unit>
    suspend fun deletePin(circleId: String, pinId: String): Result<Unit>
    /** type = null removes the user's reaction. */
    suspend fun setReaction(circleId: String, pinId: String, userId: String, type: String?)
    suspend fun toggleBeenThere(circleId: String, pinId: String, userId: String)
    suspend fun toggleWantToGo(circleId: String, pinId: String, userId: String)
}
