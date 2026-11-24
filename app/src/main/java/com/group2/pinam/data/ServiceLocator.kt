package com.group2.pinam.data

import com.group2.pinam.data.fake.FakePinRepository
import com.group2.pinam.data.firebase.FirebaseAuthRepository
import com.group2.pinam.data.firebase.FirebaseCircleRepository

/**
 * One place that decides which implementation the whole app uses.
 * Today: fake in-memory data. When a Firebase version is ready, swap ONE line here,
 * e.g.  val pins: PinRepository by lazy { FirebasePinRepository() }
 */
object ServiceLocator {
    val auth: AuthRepository by lazy { FirebaseAuthRepository() }
    val circles: CircleRepository by lazy { FirebaseCircleRepository() }
    val pins: PinRepository by lazy { FakePinRepository() }
}
