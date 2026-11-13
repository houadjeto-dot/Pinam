package com.group2.pinam.data

import com.group2.pinam.data.fake.FakeAuthRepository
import com.group2.pinam.data.fake.FakeCircleRepository
import com.group2.pinam.data.fake.FakePinRepository

/**
 * One place that decides which implementation the whole app uses.
 * Today: fake in-memory data. When a Firebase version is ready, swap ONE line here,
 * e.g.  val pins: PinRepository by lazy { FirebasePinRepository() }
 */
object ServiceLocator {
    val auth: AuthRepository by lazy { FakeAuthRepository() }
    val circles: CircleRepository by lazy { FakeCircleRepository() }
    val pins: PinRepository by lazy { FakePinRepository() }
}
