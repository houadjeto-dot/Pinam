package com.group2.pinam.data.fake

import com.group2.pinam.data.AuthRepository
import com.group2.pinam.data.CircleRepository
import com.group2.pinam.data.PinRepository
import com.group2.pinam.model.Circle
import com.group2.pinam.model.Pin
import com.group2.pinam.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeAuthRepository : AuthRepository {
    private val _user = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _user

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
        val u = User(FakeData.ME, name, email); _user.value = u; return Result.success(u)
    }
    override suspend fun signIn(email: String, password: String): Result<User> {
        val u = User(FakeData.ME, "Ama", email); _user.value = u; return Result.success(u)
    }
    override fun signOut() { _user.value = null }
}

class FakeCircleRepository : CircleRepository {
    private val circles = MutableStateFlow(FakeData.circles)

    override fun observeMyCircles(userId: String): Flow<List<Circle>> =
        circles.map { list -> list.filter { userId in it.memberIds } }

    override fun observeCircle(circleId: String): Flow<Circle?> =
        circles.map { list -> list.firstOrNull { it.id == circleId } }

    override suspend fun createCircle(name: String, owner: User): Result<Circle> {
        val c = Circle("c${circles.value.size + 1}", name, (100000..999999).random().toString(), listOf(owner.id))
        circles.update { it + c }
        return Result.success(c)
    }

    override suspend fun joinByCode(code: String, user: User): Result<Circle> {
        val found = circles.value.firstOrNull { it.inviteCode.equals(code, ignoreCase = true) }
            ?: return Result.failure(IllegalArgumentException("No circle with that code"))
        val updated = found.copy(memberIds = (found.memberIds + user.id).distinct())
        circles.update { l -> l.map { if (it.id == found.id) updated else it } }
        return Result.success(updated)
    }

    override suspend fun removeMember(circleId: String, userId: String): Result<Unit> {
        circles.update { l -> l.map { if (it.id == circleId) it.copy(memberIds = it.memberIds - userId) else it } }
        return Result.success(Unit)
    }
}

class FakePinRepository : PinRepository {
    private val pins = MutableStateFlow(FakeData.pins)

    override fun observePins(circleId: String): Flow<List<Pin>> =
        pins.map { l -> l.filter { it.circleId == circleId }.sortedByDescending { it.createdAt } }

    override fun observePin(circleId: String, pinId: String): Flow<Pin?> =
        pins.map { l -> l.firstOrNull { it.circleId == circleId && it.id == pinId } }

    override suspend fun addPin(pin: Pin, photoUri: String?): Result<Pin> {
        val saved = pin.copy(id = "p${System.currentTimeMillis()}", photoUrl = photoUri ?: "")
        pins.update { it + saved }
        return Result.success(saved)
    }

    override suspend fun updatePin(pin: Pin): Result<Unit> {
        pins.update { l -> l.map { if (it.id == pin.id) pin else it } }
        return Result.success(Unit)
    }

    override suspend fun deletePin(circleId: String, pinId: String): Result<Unit> {
        pins.update { l -> l.filterNot { it.circleId == circleId && it.id == pinId } }
        return Result.success(Unit)
    }

    private fun mutate(pinId: String, f: (Pin) -> Pin) =
        pins.update { l -> l.map { if (it.id == pinId) f(it) else it } }

    private fun toggle(list: List<String>, id: String) = if (id in list) list - id else list + id

    override suspend fun setReaction(circleId: String, pinId: String, userId: String, type: String?) =
        mutate(pinId) { p ->
            p.copy(reactions = if (type == null) p.reactions - userId else p.reactions + (userId to type))
        }

    override suspend fun toggleBeenThere(circleId: String, pinId: String, userId: String) =
        mutate(pinId) { it.copy(beenThere = toggle(it.beenThere, userId)) }

    override suspend fun toggleWantToGo(circleId: String, pinId: String, userId: String) =
        mutate(pinId) { it.copy(wantToGo = toggle(it.wantToGo, userId)) }
}
